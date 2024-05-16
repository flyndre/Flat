<script setup lang="ts">
import { getCollection } from '@/api/rest';
import {
    acceptOrDeclineAccessRequest,
    establishWebsocket,
    isAdmin,
    members,
    newInvite,
} from '@/api/websockets';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import DivisionsList from '@/components/tracking/DivisionsList.vue';
import InvitationDialog from '@/components/tracking/InvitationDialog.vue';
import JoinRequestDialog from '@/components/tracking/JoinRequestDialog.vue';
import ParticipantsList from '@/components/tracking/ParticipantsList.vue';
import { clientId } from '@/data/clientMetadata';
import { TOAST_LIFE } from '@/data/constants';
import { trackingLogs } from '@/data/trackingLogs';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { useTrackingService } from '@/service/trackingService';
import { Collection } from '@/types/Collection';
import { Division } from '@/types/Division';
import { JoinRequest } from '@/types/JoinRequest';
import { Participant } from '@/types/Participant';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { mapCenterWithDefaults } from '@/util/googleMapsUtils';
import { isOnMobile } from '@/util/mobileDetection';
import {
    mdiAccount,
    mdiAccountMultiple,
    mdiAccountPlus,
    mdiAccountTie,
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
import Button from 'primevue/button';
import Card from 'primevue/card';
import Dialog from 'primevue/dialog';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import { MenuItem } from 'primevue/menuitem';
import SelectButton from 'primevue/selectbutton';
import SplitButton from 'primevue/splitbutton';
import TabPanel from 'primevue/tabpanel';
import TabView from 'primevue/tabview';
import { useToast } from 'primevue/usetoast';
import { computed, onBeforeMount, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const props = defineProps<{
    id: string;
}>();

const router = useRouter();
const route = useRoute();
const adminView = ref(isAdmin);
const { add: pushToast } = useToast();
const {
    coords: trackingPosition,
    isActive: trackingActive,
    start: startTracking,
    stop: stopTracking,
    error: trackingError,
} = useTrackingService();

function toggleTracking() {
    trackingLoading.value = true;
    if (trackingActive.value) {
        stopTracking();
    } else {
        startTracking();
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
    label: string;
}[] = [
    {
        value: undefined,
        label: 'Unlock',
        icon: mdiHandBackRight,
    },
    {
        value: 'position',
        label: 'Location',
        icon: mdiCrosshairsGps,
    },
    {
        value: 'area',
        label: 'Area',
        icon: mdiFitToScreen,
    },
];
const mapCenterSelected = ref<undefined | 'area' | 'position'>(
    mapCenterOptions[adminView.value ? 2 : 1].value
);
const clientPos = mapCenterWithDefaults(trackingPosition, {
    lat: null,
    lng: null,
});

const adminActions: MenuItem[] = [
    {
        label: 'End Collection',
        icon: mdiStop,
        command: () => (endCollectionDialogVisible.value = true),
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
    // TODO: end collection (and fetch and display stats)
    router.push({ name: 'presets' });
}

const invitationScreenVisible = ref(false);
const invitationLink = computed(
    () => window.location.origin + import.meta.env.BASE_URL + 'join/' + props.id
);

const participants = computed<Participant[]>(() => [
    {
        clientId: '1',
        username: 'Rubi',
        color: '#64e86f',
    },
    {
        clientId: '2',
        username: 'Lubuu',
        color: '#36cfe0',
    },
]);

const joinRequests = computed<JoinRequest[]>(() => {
    // TODO: get list of join requests from service
    return [];
});
function processJoinRequest(clientId: string, accepted: boolean) {
    // TODO: send to backend and perhaps show toast
}

const tracks = computed<ParticipantTrack[]>(() => {
    return members.value;
});

const collection = ref<Collection>(undefined);
const divisions = computed<Division[]>(
    () =>
        collection.value?.divisions ?? [
            {
                id: '1',
                name: 'Norddorf',
                area: undefined,
                color: '#bfbfbf',
            },
            {
                id: '2',
                name: 'Süddorf',
                area: undefined,
                color: '#bfbfbf',
            },
        ]
);

// TODO: onMounted websocket aufmachen
onBeforeMount(async () => {
    establishWebsocket(clientId.value, route.params.id as string);

    //TODO: Errorhandling
    var response = await getCollection(
        route.params.id as string,
        clientId.value
    );

    console.log('Collection for Map:');
    console.log(response.data);
    collection.value = response.data;
    console.log(divisions);
});

const isNewInvite = newInvite;
const visible = computed(() => isNewInvite.value.length != 0 && isAdmin);

watch(members.value, () => console.log('CHANGE IN MEMBER'));
function accept() {
    acceptOrDeclineAccessRequest(
        true,
        isNewInvite.value[0].username,
        isNewInvite.value[0].clientId,
        isNewInvite.value[0].collectionId
    );
    isNewInvite.value.shift();
}
</script>

<template>
    <Dialog
        v-model:visible="visible"
        modal
        header="Accept"
        :style="{ width: '25rem' }"
    >
        {{ isNewInvite[0].username }} wants to join your Collection. Do you want
        to accept it?
        <Button
            label="Akzeptieren"
            severity="secondary"
            @click="accept"
        ></Button>
    </Dialog>
    <DefaultLayout>
        <template #action-left>
            <SplitButton
                v-if="adminView"
                :model="adminActions"
                :label="isOnMobile ? '' : 'Add Participants'"
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
                label="Leave Collection"
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
                Tracking
            </template>
            <template v-else>
                <MdiTextButtonIcon class="opacity-75" :icon="mdiPauseCircle" />
                Paused
            </template>
        </template>
        <template #action-right>
            <Button
                :label="trackingActive ? 'Pause Tracking' : 'Start Tracking'"
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
            <Dialog
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
            </Dialog>

            <InvitationDialog
                v-model:visible="invitationScreenVisible"
                :link="invitationLink"
            />

            <JoinRequestDialog
                :requests="joinRequests"
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
                            label="No"
                            severity="secondary"
                            @click="endCollectionDialogVisible = false"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiClose" />
                            </template>
                        </Button>
                        <Button
                            label="Yes"
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
                                            {{ slotProps.option.label }}
                                        </span>
                                    </div>
                                </template>
                            </SelectButton>
                            <MapWithControls
                                class="!min-h-32 !h-32 -m-2.5"
                                controls="none"
                                :center="mapCenterSelected"
                                :locked="mapCenterSelected != null"
                                :divisions
                                :client-pos
                                :tracks
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
                                :participants
                                :divisions
                                :admin-mode="isAdmin"
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
                                :participants
                                :divisions
                                :admin-mode="isAdmin"
                                @unassign-division="(d) => console.log(d)"
                                @assign-division="(d, p) => console.log(d, p)"
                            />
                        </TabPanel>
                    </TabView>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
