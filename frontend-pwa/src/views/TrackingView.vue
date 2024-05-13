<script setup lang="ts">
import { getCollection } from '@/api/rest';
import { establishWebsocket, isAdmin, newInvite } from '@/api/websockets';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import InvitationDialog from '@/components/tracking/InvitationDialog.vue';
import ParticipantsDialog from '@/components/tracking/ParticipantsDialog.vue';
import { clientId } from '@/data/clientMetadata';
import { TOAST_LIFE } from '@/data/constants';
import { trackingLogs } from '@/data/trackingLogs';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { useTrackingService } from '@/service/trackingService';
import { Division } from '@/types/Division';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { mapCenterWithDefaults } from '@/util/googleMapsUtils';
import { isOnMobile } from '@/util/mobileDetection';
import {
    mdiAccountMultiple,
    mdiAccountPlus,
    mdiCheck,
    mdiCircle,
    mdiClose,
    mdiContentCopy,
    mdiCrosshairsGps,
    mdiExport,
    mdiFitToScreen,
    mdiHandBackRight,
    mdiPause,
    mdiPauseCircle,
    mdiPlay,
    mdiShare,
    mdiStop,
} from '@mdi/js';
import { useClipboard, useShare, watchOnce } from '@vueuse/core';
import Button from 'primevue/button';
import Card from 'primevue/card';
import Dialog from 'primevue/dialog';
import { MenuItem } from 'primevue/menuitem';
import SelectButton from 'primevue/selectbutton';
import SplitButton from 'primevue/splitbutton';
import Textarea from 'primevue/textarea';
import { useToast } from 'primevue/usetoast';
import { computed, onBeforeMount, onMounted, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';

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
    {
        label: 'Manage Groups',
        icon: mdiAccountMultiple,
        command: () => (manageGroupsScreenVisible.value = true),
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
    // TODO: fetch and display stats
    router.push({ name: 'presets' });
}

const invitationScreenVisible = ref(false);
const invitationLink = ref('https://www.flat.com/join/876372894');
const manageGroupsScreenVisible = ref(false);

const tracks = computed<ParticipantTrack[]>(() => [
    {
        id: clientId.value,
        name: 'barbapapa',
        color: '#ff9922',
        progress: [
            {
                type: 'LineString',
                coordinates: trackingLogs.value?.map((l) => l.position),
            },
        ],
    },
]);

let divisions = ref([
    {
        id: 'e8581ff9-b53c-4307-aa5c-2d294b770f29',
        name: 'Dürrenmettstetten',
        color: '#1E90FF',
        area: {
            type: 'Polygon',
            coordinates: [
                [
                    [48.39479146042227, 8.57364691580809],
                    [48.39559467209357, 8.575929475679764],
                    [48.395977573415905, 8.578324688330063],
                    [48.3961592279191, 8.5818973907379],
                    [48.396034563133774, 8.583699835195908],
                    [48.39584044164562, 8.585456682100663],
                    [48.39573981874792, 8.586026651516327],
                    [48.3954183584227, 8.587594402685532],
                    [48.394392521309335, 8.590185416593918],
                    [48.393861784962056, 8.591267687931428],
                    [48.392105684967454, 8.593701792612443],
                    [48.389946988142086, 8.594557417288193],
                    [48.38765282711779, 8.594965113058457],
                    [48.384161514074385, 8.593581093206772],
                    [48.38286467965901, 8.591845703974137],
                    [48.38173882958217, 8.589659703627],
                    [48.38084810776395, 8.586813879862198],
                    [48.38012839315009, 8.582047594442734],
                    [48.38020054321759, 8.57834212268866],
                    [48.38063611367502, 8.576331807031998],
                    [48.38236945120565, 8.572729600324998],
                    [48.38344362767722, 8.571192694559464],
                    [48.384774292467746, 8.569859636679062],
                    [48.3862955185641, 8.568797481909165],
                    [48.38803400699896, 8.568094743147263],
                    [48.38950704447006, 8.568108154192338],
                    [48.39060956819524, 8.568475616827378],
                    [48.39201306949216, 8.569569958105454],
                    [48.39348777280142, 8.571071995153794],
                ],
            ],
        },
    },
]);


// TODO: onMounted websocket aufmachen
onBeforeMount(async () => {

    establishWebsocket(clientId.value, route.params.id as string);


    //TODO: Errorhandling
    var response = await getCollection(route.params.id as string, clientId.value);
    
    console.log("Collection for Map:")
    console.log(response.data)
    divisions.value = response.data.collectionDivision
    console.log(divisions)
    


    

})

const isNewInvite = ref(newInvite.value)

</script>

<template>
    

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
                    {{ locationError }}
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

            <!-- Participant Management Dialog -->
            <ParticipantsDialog
                v-model:visible="manageGroupsScreenVisible"
                :participants="[]"
            />
            <!-- <Dialog
                :position="isOnMobile ? 'bottom' : 'top'"
                v-model:visible="manageGroupsScreenVisible"
                header="Manage Participants"
                :draggable="false"
                :closable="false"
                modal
            >
                <template #default> (list of participants) </template>
                <template #footer>
                    <div class="w-full flex flex-row justify-center gap-2">
                        <Button
                            label="Close"
                            severity="secondary"
                            text
                            @click="manageGroupsScreenVisible = false"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiClose" />
                            </template>
                        </Button>
                    </div>
                </template>
            </Dialog> -->

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

            <!-- <div
                class="w-full h-full bg-gray-200 flex flex-col items-center justify-center text-gray-500 rounded-md gap-2"
                :class="{ 'mb-2': !isOnMobile }"
            >
                Map Placeholder
                <div
                    class="flex flex-col border border-solid rounded-md py-1 px-2"
                    :class="{ 'opacity-30': !trackingActive }"
                >
                    {{ trackingLogs }}
                </div>
            </div> -->

            <Card
                class="h-full grow"
                :pt="{
                    root: {
                        class: [
                            'overflow-hidden flex',
                            { 'flex flex-col-reverse': !isOnMobile },
                        ],
                    },
                    header: {
                        class: 'h-full flex flex-col grow',
                    },
                    body: { class: 'p-2.5' },
                }"
            >
                <template #header v-if="!loading">
                    <MapWithControls
                        controls="none"
                        :center="mapCenterSelected"
                        :locked="mapCenterSelected != null"
                        :divisions
                        :client-pos
                        :tracks
                    />
                </template>
                <template #content>
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
                                <MdiIcon :icon="slotProps.option.icon" />
                                <span
                                    class="max-[400px]:hidden text-ellipsis overflow-hidden z-10"
                                >
                                    {{ slotProps.option.label }}
                                </span>
                            </div>
                        </template>
                    </SelectButton>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
