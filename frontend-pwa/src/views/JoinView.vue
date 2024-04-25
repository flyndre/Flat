<script setup lang="ts">
import CardProgressIndicator from '@/components/card/CardProgressIndicator.vue';
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import getJoinId from '@/util/getJoinId';
import { isOnMobile } from '@/util/mobileDetection';
import validateJoinLink from '@/validation/validateJoinLink';
import validateJoinName from '@/validation/validateJoinName';
import {
    mdiAccount,
    mdiArrowLeft,
    mdiClose,
    mdiImport,
    mdiLink,
} from '@mdi/js';
import { useTimeoutFn } from '@vueuse/core';
import Button from 'primevue/button';
import Card from 'primevue/card';
import Dialog from 'primevue/dialog';
import IconField from 'primevue/iconfield';
import InputText from 'primevue/inputtext';
import { computed, ref, watchEffect } from 'vue';
import { useRouter } from 'vue-router';
import { useDevicesList, useUserMedia } from '@vueuse/core';

const props = defineProps<{
    id?: string;
}>();

const router = useRouter();
const joinLink = ref(props.id ? window.location.href : '');
const joinId = computed(() => getJoinId(joinLink.value));
const joinName = ref('');
const submittable = computed(
    () => validateJoinLink(joinLink.value) && validateJoinName(joinName.value)
);
const dialogVisible = ref(false);
const { start, stop } = useTimeoutFn(
    () => {
        // TODO: create WS connection etc.
        router.push({ name: 'track' });
    },
    3000,
    {
        immediate: false,
    }
);
function join() {
    dialogVisible.value = true;
    start();
    // TODO: send request
}
function cancel() {
    dialogVisible.value = false;
    stop();
    // TODO: cancel request or send cancelation request
}
const currentCamera = ref<string>();
const { videoInputs: cameras, permissionGranted } = useDevicesList({
    requestPermissions: true,
    onUpdated() {
        if (!cameras.value.find((i) => i.deviceId === currentCamera.value))
            currentCamera.value = cameras.value[0]?.deviceId;
    },
});

const video = ref<HTMLVideoElement>();
const {
    stream,
    enabled,
    start: startUserMedia,
    stop: stopUserMedia,
} = useUserMedia({
    constraints: { video: { deviceId: currentCamera.value } },
});

watchEffect(() => {
    if (video.value) {
        video.value.srcObject = stream.value;
        startUserMedia();
    }
});
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'home' }">
                <Button label="Back" severity="secondary" text>
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> Join a Collection </template>
        <template #action-right>
            <Button
                label="Join"
                severity="primary"
                :disabled="!submittable"
                @click="join"
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiImport" />
                </template>
            </Button>
        </template>
        <template #default>
            <Dialog
                v-model:visible="dialogVisible"
                :closable="false"
                :draggable="false"
                modal
                :position="isOnMobile ? 'bottom' : 'top'"
                class="overflow-hidden"
                header="Waiting to join..."
            >
                <CardProgressIndicator mode="indeterminate" />
                Please stand by as the collection's admin reviews your join
                request.
                <template #footer>
                    <div class="w-full flex flex-row justify-center">
                        <Button
                            label="Cancel request"
                            severity="danger"
                            text
                            @click="cancel"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiClose" />
                            </template>
                        </Button>
                    </div>
                </template>
            </Dialog>
            <Card :pt="{ root: { class: 'overflow-hidden' } }">
                <template #header>
                    <div
                        v-if="!id"
                        class="w-full h-[40vh] bg-gray-500 bg-opacity-50 flex flex-col justify-center items-center select-none"
                    >
                        <div
                            v-if="permissionGranted"
                            class="w-full h-full relative"
                        >
                            <video
                                ref="video"
                                class="w-full h-full object-cover -scale-x-100"
                                autoplay
                                muted
                            />
                            <div
                                class="absolute top-0 bottom-0 left-0 right-0 flex flex-row justify-center align-center"
                            >
                                <div
                                    class="m-2.5 aspect-square border-solid border-2.5 rounded border-pink-600 border-opacity-50 flex flex-col items-center justify-end p-2.5"
                                >
                                    <span
                                        class="text-pink-600 font-bold opacity-75"
                                    >
                                        Scan a QR Code
                                    </span>
                                </div>
                            </div>
                        </div>
                        <span v-else class="text-gray-500">
                            To scan a QR code, allow this app to use your
                            camera.
                        </span>
                    </div>
                </template>
                <template #content>
                    <div class="flex flex-col gap-2.5">
                        <IconField iconPosition="left">
                            <MdiInputIcon :icon="mdiLink" />
                            <InputText
                                class="w-full"
                                placeholder="Or enter a link manually"
                                v-model="joinLink"
                                :disabled="id !== undefined"
                            />
                        </IconField>
                        <IconField iconPosition="left">
                            <MdiInputIcon :icon="mdiAccount" />
                            <InputText
                                class="w-full"
                                placeholder="Your Name"
                                v-model="joinName"
                            />
                        </IconField>
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
