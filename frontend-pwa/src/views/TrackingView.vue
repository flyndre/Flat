<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import InvitationDialog from '@/components/tracking/InvitationDialog.vue';
import JoinRequestDialog from '@/components/tracking/JoinRequestDialog.vue';
import ParticipantsList from '@/components/tracking/ParticipantsList.vue';
import { TOAST_LIFE } from '@/data/constants';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { useCollectionService } from '@/service/collectionService';
import { useTrackingService } from '@/service/trackingService';
import { JoinRequest } from '@/types/JoinRequest';
import { mapCenterWithDefaults } from '@/util/googleMapsUtils';
import { isOnMobile } from '@/util/mobileDetection';
import {
    mdiAccountMultiple,
    mdiAccountPlus,
    mdiCheck,
    mdiCircle,
    mdiClose,
    mdiCrosshairsGps,
    mdiExport,
    mdiFitToScreen,
    mdiHandBackRight,
    mdiMap,
    mdiPause,
    mdiPauseCircle,
    mdiPlay,
    mdiStop,
    mdiTextureBox,
} from '@mdi/js';

import TabPanel from 'primevue/tabpanel';
import Button from 'primevue/button';
import Card from 'primevue/card';
import Dialog from 'primevue/dialog';
import { MenuItem } from 'primevue/menuitem';
import SelectButton from 'primevue/selectbutton';
import SplitButton from 'primevue/splitbutton';
import TabView from 'primevue/tabview';
import { useToast } from 'primevue/usetoast';
import { computed, onBeforeMount, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import DivisionsList from '@/components/tracking/DivisionsList.vue';
import { useI18n } from 'vue-i18n';

const props = defineProps<{
    id: string;
}>();

const { t } = useI18n();
const router = useRouter();
const route = useRoute();
const { add: pushToast } = useToast();
const {
    coords: trackingPosition,
    isActive: trackingActive,
    start: startTrackingLogs,
    stop: stopTrackingLogs,
    error: trackingError,
} = useTrackingService();

function toggleTracking() {
    trackingLoading.value = true;
    if (trackingActive.value) {
        stopTrackingLogs();
        stopTrackingCollection();
    } else {
        startTrackingLogs();
        startTrackingCollection();
    }
    setTimeout(() => {
        trackingLoading.value = false;
    }, 1000);
}

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
        command: () => (endCollectionDialogVisible.value = true),
    },
    {
        label: t('tracking.action_manage'),
        icon: mdiAccountMultiple,
        command: () => (manageParticipantsDialogVisible.value = true),
    },
];

function leaveCollection() {
    pushToast({
        summary: `You left ${'<Collection Name>'}`,
        severity: 'warn',
        closable: true,
        life: TOAST_LIFE,
    });
    router.push({ name: 'home' });
}

const endCollectionDialogVisible = ref(false);
function stopCollection() {
    closeCollection(route.params.id as string);
    // TODO: end collection (and fetch and display stats)
    router.push({ name: 'presets' });
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
    member,
    requests,
    startTracking: startTrackingCollection,
    stopTracking: stopTrackingCollection,
} = useCollectionService(props.id);

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
</script>

<template>
    {{ activeCollection }}
    <JoinRequestDialog
        :requests="requests"
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
                @click="leaveCollection"
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
            <template v-if="trackingActive">
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
                    trackingActive
                        ? $t('tracking.action_pause_tracking')
                        : $t('tracking.action_start_tracking')
                "
                :loading="trackingLoading"
                :disabled="locationError"
                @click="toggleTracking"
            >
                <template #icon>
                    <MdiTextButtonIcon
                        :icon="trackingActive ? mdiPause : mdiPlay"
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
                :requests="requests"
                @request-answered="processJoinRequest"
            />

            <!-- Stop Collection Confirm Dialog -->
            <Dialog
                class="max-w-[750px]"
                :position="isOnMobile ? 'bottom' : 'top'"
                v-model:visible="endCollectionDialogVisible"
                header="End Collection"
                :draggable="false"
                :closable="false"
                modal
            >
                <template #default>
                    Are you certain that you want to end the ongoing collection?
                    <br />
                    <span class="text-red-500">
                        This action is irreversible.
                    </span>
                    It will stop the tracking of all participants and save the
                    tracked movement on your device only.
                </template>
                <template #footer>
                    <div class="w-full flex flex-row justify-center gap-2">
                        <Button
                            :label="$t('universal.deny')"
                            severity="secondary"
                            @click="endCollectionDialogVisible = false"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiClose" />
                            </template>
                        </Button>
                        <Button
                            :label="$t('universal.confirm')"
                            severity="danger"
                            @click="stopCollection"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiCheck" />
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
                            <SelectButton
                                class="flex w-full flex-row"
                                v-model="mapCenterSelected"
                                :options="mapCenterOptions"
                                :option-value="(o) => o.value"
                                :allow-empty="false"
                                :pt="{ button: { class: 'w-full' } }"
                            >
                                <template #option="slotProps">
                                    <div
                                        class="flex flex-row justify-center items-center flex-nowrap w-full gap-3 min-h-6"
                                    >
                                        <MdiIcon
                                            :icon="slotProps.option.icon"
                                        />
                                        <span
                                            class="max-[400px]:hidden text-ellipsis overflow-hidden z-10"
                                        >
                                            {{
                                                $t(slotProps.option.messageCode)
                                            }}
                                        </span>
                                    </div>
                                </template>
                            </SelectButton>
                            <MapWithControls
                                class="!min-h-32 !h-32 -m-2.5"
                                controls="none"
                                :center="mapCenterSelected"
                                :locked="mapCenterSelected != null"
                                :divisions="activeCollection.divisions"
                                :client-pos
                                :tracks="member"
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
                                :participants="member"
                                :divisions="activeCollection.divisions"
                                :admin-mode="isAdmin"
                                @kick-participant="(p) => console.log(p)"
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
                                :participants="member"
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
