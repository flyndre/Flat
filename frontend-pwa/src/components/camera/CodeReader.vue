<script setup lang="ts">
import Dropdown from 'primevue/dropdown';
import { ref, onMounted } from 'vue';
import { QrcodeStream } from 'vue-qrcode-reader';
import { mdiCamera, mdiCameraAccount } from '@mdi/js';
import MdiTextButtonIcon from '../icons/MdiTextButtonIcon.vue';
import ProgressSpinner from 'primevue/progressspinner';

/*** detection handling ***/

const emit = defineEmits<{
    scanned: [values: string[]];
}>();

function onDetect(detectedCodes) {
    emit(
        'scanned',
        detectedCodes.map((code) => code.rawValue)
    );
}

const loading = ref(true);

/*** select camera ***/

const selectedDevice = ref<MediaDeviceInfo>(undefined);
const devices = ref<MediaDeviceInfo[]>([]);

onMounted(async () => {
    devices.value = (await navigator.mediaDevices.enumerateDevices()).filter(
        ({ kind }) => kind === 'videoinput'
    );

    if (devices.value.length > 0) {
        selectedDevice.value = devices.value.toReversed()[0];
    }
});

/*** error handling ***/

const error = ref<string>(null);

function onError(e) {
    loading.value = false;
    error.value =
        Object.entries({
            NotAllowedError:
                'To scan a QR code, allow this app to use your camera and reload.',
            NotFoundError: 'No camera on this device',
            NotSupportedError: 'Secure context required (HTTPS, localhost)',
            NotReadableError: 'Is the camera already in use?',
            OverconstrainedError: 'Installed cameras are not suitable',
            StreamApiNotSupportedError:
                'Stream API is not supported in this browser',
            InsecureContextError:
                'Camera access is only permitted in secure context. Use HTTPS or localhost rather than HTTP.',
        }).find((entry) => entry[0] === e.name)[1] ?? e.message;
}
</script>

<template>
    <div class="w-full h-full relative">
        <QrcodeStream
            class="w-full h-full object-cover"
            :constraints="{
                deviceId: selectedDevice?.deviceId,
            }"
            @error="onError"
            @detect="onDetect"
            @camera-on="
                error = null;
                loading = false;
            "
            @camera-off="loading = true"
        />
        <div
            class="absolute top-0 bottom-0 left-0 right-0 flex flex-row justify-center items-center"
        >
            <ProgressSpinner
                v-if="loading"
                class="[&_*]:!stroke-pink-500 w-12 h-12"
                stroke-width="0.35rem"
            />
            <span v-else-if="error != null" class="p-6 opacity-50 text-center">
                {{ error }}
            </span>
            <div
                v-else
                class="h-[90%] aspect-square border-solid border-[3px] rounded border-pink-600 border-opacity-50 flex flex-col items-center justify-between p-2.5"
            >
                <span class="text-pink-600 font-semibold opacity-75">
                    Scan a QR Code
                </span>
                <Dropdown
                    class="w-full"
                    v-model="selectedDevice"
                    :options="devices"
                    option-label="label"
                >
                    <template #value="slotProps">
                        <MdiTextButtonIcon
                            :icon="
                                slotProps.value.label
                                    ?.toLowerCase()
                                    ?.includes('front')
                                    ? mdiCameraAccount
                                    : mdiCamera
                            "
                        />
                        {{ slotProps.value.label }}
                    </template>
                    <template #option="slotProps">
                        <MdiTextButtonIcon
                            :icon="
                                slotProps.option.label
                                    ?.toLowerCase()
                                    ?.includes('front')
                                    ? mdiCameraAccount
                                    : mdiCamera
                            "
                        />
                        {{ slotProps.option.label }}
                    </template>
                </Dropdown>
            </div>
        </div>
    </div>
</template>
