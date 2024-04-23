<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import { clientId } from '@/data/clientMetadata';
import { TOAST_LIFE } from '@/data/constants';
import { trackingLogs } from '@/data/trackingLogs';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { useTrackingService } from '@/service/trackingService';
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
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const adminView = ref(true);
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

const { isSupported: copySupported, copy, copied } = useClipboard();
function copyInvitationLink() {
    copy(invitationLink.value);
    watchOnce(copied, () => {
        pushToast({
            summary: 'Invitation link copied!',
            severity: 'success',
            closable: true,
            life: TOAST_LIFE,
        });
    });
}

const { isSupported: shareSupported, share } = useShare({
    url: invitationLink.value,
    title: `Join ${'<Collection Name>'}`,
    text: '...',
});
function shareInvitationLink() {
    share();
}

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

            <!-- Invitation Dialog -->
            <Dialog
                :position="isOnMobile ? 'bottom' : 'top'"
                v-model:visible="invitationScreenVisible"
                header="Add Participants"
                :draggable="false"
                :closable="false"
                modal
            >
                <template #default>
                    <div class="flex flex-col gap-3">
                        <span>
                            To invite people to parttake in your collection
                            campaign, share this invitation link:
                        </span>
                        <Textarea
                            class="w-full text-center"
                            readonly
                            auto-resize
                            v-model="invitationLink"
                        />
                    </div>
                </template>
                <template #footer>
                    <div class="w-full flex flex-row justify-center gap-2">
                        <Button
                            label="Close"
                            severity="secondary"
                            text
                            @click="invitationScreenVisible = false"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiClose" />
                            </template>
                        </Button>
                        <div class="grow"></div>
                        <Button
                            v-if="copySupported"
                            :text="shareSupported"
                            label="Copy"
                            @click="copyInvitationLink"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiContentCopy" />
                            </template>
                        </Button>
                        <Button
                            v-if="shareSupported"
                            label="Share"
                            @click="shareInvitationLink"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiShare" />
                            </template>
                        </Button>
                    </div>
                </template>
            </Dialog>

            <!-- Participant Management Dialog -->
            <!-- <ParticipantsDialog v-model:visible="manageGroupsScreenVisible" /> -->
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
                    root: { class: 'overflow-hidden' },
                    header: {
                        class: 'h-full flex flex-col grow',
                    },
                    body: { class: 'p-2.5' },
                }"
            >
                <template #header>
                    <MapWithControls
                        controls="none"
                        :center="mapCenterSelected"
                        :locked="mapCenterSelected != null"
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
