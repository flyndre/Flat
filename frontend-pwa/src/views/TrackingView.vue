<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import DivisionsList from '@/components/tracking/DivisionsList.vue';
import InvitationDialog from '@/components/tracking/InvitationDialog.vue';
import JoinRequestDialog from '@/components/tracking/JoinRequestDialog.vue';
import ParticipantsList from '@/components/tracking/ParticipantsList.vue';
import { clientId } from '@/data/clientMetadata';
import { lastActiveCollection } from '@/data/collections';
import { collectionStatsDB } from '@/data/collectionStats';
import { TOAST_LIFE } from '@/data/constants';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { useCollectionService } from '@/service/collectionService';
import { useTrackingService } from '@/service/trackingService';
import { JoinRequest } from '@/types/JoinRequest';
import { Participant } from '@/types/Participant';
import { dbSafe } from '@/util/dbUtils';
import { mapCenterWithDefaults } from '@/util/googleMapsUtils';
import { isOnMobile } from '@/util/mobileDetection';
import { calculateCollectionStats } from '@/util/statsUtils';
import {
    mdiAccountMultiple,
    mdiAccountPlus,
    mdiCircle,
    mdiClose,
    mdiCrosshairsGps,
    mdiEarth,
    mdiEarthPlus,
    mdiExport,
    mdiFitToScreen,
    mdiHandBackRight,
    mdiMap,
    mdiMenu,
    mdiPause,
    mdiPauseCircle,
    mdiPlay,
    mdiRoadVariant,
    mdiStop,
    mdiTerrain,
    mdiTextureBox,
} from '@mdi/js';
import { watchOnce } from '@vueuse/core';
import Button from 'primevue/button';
import Card from 'primevue/card';
import Dialog from 'primevue/dialog';
import Menu from 'primevue/menu';
import { MenuItem } from 'primevue/menuitem';
import SelectButton from 'primevue/selectbutton';
import SplitButton from 'primevue/splitbutton';
import TabPanel from 'primevue/tabpanel';
import TabView from 'primevue/tabview';
import { useToast } from 'primevue/usetoast';
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

const props = defineProps<{
    id: string;
}>();
const wakeLock = ref<WakeLockSentinel | null>(null);
const { t } = useI18n();
const router = useRouter();
const { add: pushToast } = useToast();
const {
    coords: trackingPosition,
    isActive: trackingLogsActive,
    start: startTrackingLogs,
    stop: stopTrackingLogs,
    error: trackingError,
} = useTrackingService();

const trackingLoading = ref(false);
const locationError = computed(
    () => trackingError.value !== undefined && trackingError.value?.code > 0
);
const mapCenterOptions: {
    value?: 'area' | 'position';
    icon: string;
    messageCode: string;
}[] = [
    {
        value: undefined,
        messageCode: 'tracking.unlock_focus',
        icon: mdiHandBackRight,
    },
    {
        value: 'position',
        messageCode: 'tracking.location_focus',
        icon: mdiCrosshairsGps,
    },
    {
        value: 'area',
        messageCode: 'tracking.area_focus',
        icon: mdiFitToScreen,
    },
];

const adminActions: MenuItem[] = [
    {
        label: t('tracking.action_end'),
        icon: mdiStop,
        disabled: () => closeCollectionLoading.value,
        command: () => (closeCollectionDialogVisible.value = true),
    },
    {
        label: t('tracking.action_manage'),
        icon: mdiAccountMultiple,
        command: () => (manageParticipantsDialogVisible.value = true),
    },
];

function _clearUpBeforeLeave() {
    stopTrackingLogs();
    stopTrackingCollection();
    lastActiveCollection.set(undefined);
}

async function leaveCollectionHandler() {
    if (!confirm(t('tracking.action_leave_warning'))) return;
    try {
        const response = await leave(props.id, clientId.value);
        if (response.status == 200) {
            pushToast({
                summary: t('tracking.leave_success', {
                    collectionName: activeCollection.value?.name,
                }),
                severity: 'info',
                closable: true,
                life: TOAST_LIFE,
            });
            _clearUpBeforeLeave();
            router.push({ name: 'home' });
        } else {
            throw response.statusText;
        }
    } catch (error) {
        pushToast({
            summary: t('tracking.leave_failed'),
            severity: 'error',
            closable: true,
            life: TOAST_LIFE,
        });
    }
}

const closeCollectionDialogVisible = ref(false);
const closeCollectionLoading = ref(false);
async function closeCollectionNow() {
    closeCollectionLoading.value = true;
    try {
        const stats = calculateCollectionStats(activeCollection.value);
        await collectionStatsDB.add(dbSafe(stats));
        closeCollection(props.id);
        pushToast({
            life: TOAST_LIFE,
            severity: 'success',
            summary: t('tracking.close_success'),
        });
        _clearUpBeforeLeave();
        router.push({
            name: 'edit',
            params: { id: props.id },
            query: { stats: stats.id },
        });
    } catch (e) {
        pushToast({
            life: TOAST_LIFE,
            severity: 'error',
            summary: t('tracking.close_failed'),
        });
    }
    closeCollectionLoading.value = false;
}

const invitationScreenVisible = ref(false);
const invitationLink = computed(
    () => window.location.origin + import.meta.env.BASE_URL + 'join/' + props.id
);

const manageParticipantsDialogVisible = ref(false);

const {
    activeCollection,
    assignDivision,
    closeCollection,
    connectionStatus,
    handleRequest,
    isAdmin,
    isLoading,
    isTracking: collectionTrackingActive,
    startTracking: startTrackingCollection,
    stopTracking: stopTrackingCollection,
    kick,
    leave,
    kickMessage,
    latestLeavedUser,
    collectionClosed,
} = useCollectionService(props.id);

watch(collectionClosed, (value) => {
    pushToast({
        summary: t('tracking.close_notice'),
        severity: 'info',
        life: TOAST_LIFE,
    });
    router.push({ name: 'home' });
});

watch(latestLeavedUser, (value) => {
    pushToast({
        summary: t('tracking.leave_notice', { name: value }),
        severity: 'info',
        life: TOAST_LIFE,
    });
});

watch(kickMessage, (value) => {
    pushToast({
        summary: t('tracking.kick_notice'),
        severity: 'error',
        life: TOAST_LIFE,
    });
    router.push({ name: 'home' });
});

function processJoinRequest(joinRequest: JoinRequest) {
    handleRequest(
        joinRequest.accepted,
        joinRequest.username,
        joinRequest.clientId,
        joinRequest.collectionId
    );
}

const mapCenterSelected = ref<undefined | 'area' | 'position'>(
    mapCenterOptions[isAdmin.value ? 2 : 1].value
);
const clientPos = mapCenterWithDefaults(trackingPosition, {
    lat: null,
    lng: null,
});

const isTracking = computed(
    () => trackingLogsActive.value || collectionTrackingActive.value
);

function toggleTracking() {
    trackingLoading.value = true;
    watchOnce(isTracking, () => {
        setTimeout(() => (trackingLoading.value = false), 1000);
    });
    if (isTracking.value) {
        stopTrackingLogs();
        stopTrackingCollection();
    } else {
        startTrackingLogs();
        startTrackingCollection();
    }
}

async function kickParticipant(collectionId: string, participant: Participant) {
    try {
        const response = await kick(collectionId, participant.id);
        if (response.status !== 200) {
            throw response;
        }
        pushToast({
            summary: t('tracking.kick_success', {
                participantName: participant.name,
            }),
            severity: 'success',
            life: TOAST_LIFE,
        });
    } catch (error) {
        pushToast({
            summary: t('tracking.kick_failed', {
                participantName: participant.name,
            }),
            severity: 'error',
            life: TOAST_LIFE,
        });
    }
}

const mapTypeId = ref<`${google.maps.MapTypeId}`>('roadmap');
const mapTypeMenu = ref<InstanceType<typeof Menu>>(null);
function toggleMapTypeMenu(e: Event) {
    mapTypeMenu.value.toggle(e);
}
const mapTypeOptions: MenuItem[] = [
    {
        value: 'roadmap',
        icon: mdiRoadVariant,
        command: () => (mapTypeId.value = 'roadmap'),
    },
    {
        value: 'terrain',
        icon: mdiTerrain,
        command: () => (mapTypeId.value = 'terrain'),
    },
    {
        value: 'satellite',
        icon: mdiEarth,
        command: () => (mapTypeId.value = 'satellite'),
    },
    {
        value: 'hybrid',
        icon: mdiEarthPlus,
        command: () => (mapTypeId.value = 'hybrid'),
    },
];

onMounted(async () => {
    try {
        if ('wakeLock' in navigator) {
            wakeLock.value = await navigator.wakeLock.request('screen');
            console.debug('Got Wakelock');
        } else {
            console.warn('Wake Lock API not supported on this browser.');
        }
    } catch (err) {
        console.debug('Error at Requesting of WakeLock');
    }
});

onBeforeUnmount(async () => {
    if (wakeLock.value !== null) {
        await wakeLock.value.release();
        wakeLock.value = null;
        console.debug('Screen Wake Lock released');
    }
});
</script>

<template>
    <JoinRequestDialog
        :requests="activeCollection.requestedUsers"
        @request-answered="processJoinRequest"
    />
    <DefaultLayout>
        <template #action-left>
            <SplitButton
                v-if="isAdmin"
                :model="adminActions"
                :label="isOnMobile ? '' : $t('tracking.action_invite')"
                severity="secondary"
                @click="invitationScreenVisible = true"
            >
                <template #icon>
                    <MdiTextButtonIcon
                        :class="{ '!mr-0': isOnMobile }"
                        :icon="mdiAccountPlus"
                    />
                </template>
                <template #menuitemicon="slotProps">
                    <MdiTextButtonIcon :icon="slotProps.item.icon" />
                </template>
            </SplitButton>

            <Button
                v-else
                :label="$t('tracking.action_leave')"
                severity="secondary"
                @click="leaveCollectionHandler()"
            >
                <template #icon>
                    <MdiTextButtonIcon
                        class="rotate-180 mr-3.5"
                        :icon="mdiExport"
                    />
                </template>
            </Button>
        </template>
        <template #title>
            <template v-if="isTracking">
                <MdiTextButtonIcon
                    class="text-red-500 animate-ping"
                    :icon="mdiCircle"
                />
                {{ $t('tracking.title_active') }}
            </template>
            <template v-else>
                <MdiTextButtonIcon class="opacity-75" :icon="mdiPauseCircle" />
                {{ $t('tracking.title_paused') }}
            </template>
        </template>
        <template #action-right>
            <Button
                :label="
                    isTracking
                        ? $t('tracking.action_pause_tracking')
                        : $t('tracking.action_start_tracking')
                "
                :loading="trackingLoading"
                :disabled="locationError"
                @click="toggleTracking"
            >
                <template #icon>
                    <MdiTextButtonIcon
                        :icon="isTracking ? mdiPause : mdiPlay"
                    />
                </template>
            </Button>
        </template>
        <template #default>
            <!-- Geolocation Permission Dialog -->
            <!-- <Dialog
                :position="isOnMobile ? 'bottom' : 'top'"
                :visible="locationError"
                header="Location Permission Required"
                :draggable="false"
                :closable="false"
                modal
            >
                <template #default>
                    To use this app, it is required that you grant the location
                    permission.
                    {{ trackingError }}
                </template>
                <template #footer>
                    <div class="w-full flex flex-row justify-center gap-2">
                        <Button
                            label="Leave Collection"
                            severity="secondary"
                            text
                        >
                            <template #icon>
                                <MdiTextButtonIcon
                                    class="rotate-180 mr-3"
                                    :icon="mdiExport"
                                />
                            </template>
                        </Button>
                    </div>
                </template>
            </Dialog> -->

            <InvitationDialog
                v-model:visible="invitationScreenVisible"
                :link="invitationLink"
            />

            <JoinRequestDialog
                :requests="activeCollection.requestedUsers"
                @request-answered="processJoinRequest"
            />

            <!-- Stop Collection Confirm Dialog -->
            <Dialog
                class="max-w-[750px]"
                :position="isOnMobile ? 'bottom' : 'top'"
                v-model:visible="closeCollectionDialogVisible"
                header="End Collection"
                :draggable="false"
                :closable="false"
                modal
            >
                <template #default>
                    {{ $t('tracking.action_end_warning.0') }}
                    <br />
                    <span class="text-red-500">
                        {{ $t('tracking.action_end_warning.1') }}
                    </span>
                    <br />
                    {{ $t('tracking.action_end_warning.2') }}
                </template>
                <template #footer>
                    <div class="w-full flex flex-row justify-center gap-2">
                        <Button
                            :label="$t('universal.cancel')"
                            severity="secondary"
                            @click="closeCollectionDialogVisible = false"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiClose" />
                            </template>
                        </Button>
                        <Button
                            :label="$t('tracking.action_end_confirm')"
                            severity="danger"
                            @click="closeCollectionNow"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiStop" />
                            </template>
                        </Button>
                    </div>
                </template>
            </Dialog>

            <Card
                class="h-full basis-0 grow"
                :pt="{
                    body: {
                        class: [
                            'p-2.5 grow',
                            { 'pb-0': isOnMobile },
                            { 'pt-0': !isOnMobile },
                        ],
                    },
                    content: {
                        class: 'grow justify-end',
                    },
                }"
            >
                <template #content>
                    <TabView
                        :pt="{
                            root: {
                                class: [
                                    'flex grow h-full',
                                    isOnMobile
                                        ? 'flex-col-reverse'
                                        : 'flex-col',
                                ],
                            },
                            nav: {
                                class: [isOnMobile ? 'mt-2' : 'mb-2'],
                            },
                            inkbar: { class: 'rounded-t h-1' },
                            panelContainer: {
                                class: 'p-0 grow flex flex-col justify-stretch',
                            },
                        }"
                    >
                        <TabPanel
                            :pt="{
                                content: {
                                    class: [
                                        'grow flex justify-start gap-5',
                                        isOnMobile
                                            ? 'flex-col-reverse'
                                            : 'flex-col',
                                    ],
                                },
                            }"
                        >
                            <template #header>
                                <div class="flex justify-center items-center">
                                    <MdiTextButtonIcon :icon="mdiMap" />
                                    Map
                                </div>
                            </template>
                            <div
                                class="flex flex-row gap-2 items-center justify-stretch flex-wrap"
                            >
                                <SelectButton
                                    class="basis-7/12"
                                    v-model="mapCenterSelected"
                                    :options="mapCenterOptions"
                                    :option-value="(o) => o.value"
                                    :allow-empty="false"
                                    :pt="{
                                        button: {
                                            class: 'h-9 w-auto grow flex flex-row justify-center',
                                        },
                                        root: {
                                            class: 'w-auto flex flex-row grow justify-stretch',
                                        },
                                    }"
                                >
                                    <template #option="slotProps">
                                        <div
                                            class="flex flex-row justify-center items-center flex-nowrap w-full gap-3"
                                        >
                                            <MdiIcon
                                                :icon="slotProps.option.icon"
                                            />
                                            <span
                                                class="max-[400px]:hidden text-ellipsis overflow-hidden z-10"
                                            >
                                                {{
                                                    $t(
                                                        slotProps.option
                                                            .messageCode
                                                    )
                                                }}
                                            </span>
                                        </div>
                                    </template>
                                </SelectButton>
                                <Button
                                    severity="secondary"
                                    @click="toggleMapTypeMenu"
                                >
                                    <template #icon>
                                        <MdiIcon :icon="mdiMenu" />
                                    </template>
                                </Button>
                                <Menu
                                    ref="mapTypeMenu"
                                    :model="mapTypeOptions"
                                    popup
                                    :pt="{
                                        root: {
                                            class: 'min-w-0 cursor-pointer',
                                        },
                                    }"
                                >
                                    <template #item="slotProps">
                                        <MdiIcon
                                            class="m-3"
                                            :icon="slotProps.item.icon"
                                        />
                                    </template>
                                </Menu>
                            </div>
                            <MapWithControls
                                class="!min-h-32 !h-32 -m-2.5"
                                controls="none"
                                :map-type="mapTypeId"
                                :center="mapCenterSelected"
                                :locked="mapCenterSelected != null"
                                :divisions="activeCollection.divisions"
                                :client-pos
                                :tracks="activeCollection.confirmedUsers"
                            />
                        </TabPanel>
                        <TabPanel
                            :pt="{
                                content: {
                                    class: [
                                        'grow flex justify-start gap-5',
                                        isOnMobile
                                            ? 'flex-col-reverse'
                                            : 'flex-col',
                                    ],
                                },
                            }"
                        >
                            <template #header>
                                <div class="flex justify-center items-center">
                                    <MdiTextButtonIcon
                                        :icon="mdiAccountMultiple"
                                    />
                                    Participants
                                </div>
                            </template>
                            <ParticipantsList
                                :participants="activeCollection.confirmedUsers"
                                :divisions="activeCollection.divisions"
                                :admin-mode="isAdmin"
                                @kick-participant="
                                    (p) =>
                                        kickParticipant(activeCollection.id, p)
                                "
                            />
                        </TabPanel>
                        <TabPanel
                            :pt="{
                                content: {
                                    class: [
                                        'grow flex justify-start gap-5',
                                        isOnMobile
                                            ? 'flex-col-reverse'
                                            : 'flex-col',
                                    ],
                                },
                            }"
                        >
                            <template #header>
                                <div class="flex justify-center items-center">
                                    <MdiTextButtonIcon :icon="mdiTextureBox" />
                                    Divisions
                                </div>
                            </template>
                            <DivisionsList
                                :participants="activeCollection.confirmedUsers"
                                :divisions="activeCollection.divisions"
                                :admin-mode="isAdmin"
                                @unassign-division="
                                    (d) => assignDivision(d, null)
                                "
                                @assign-division="
                                    (d, p) => assignDivision(d, p)
                                "
                            />
                        </TabPanel>
                    </TabView>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
