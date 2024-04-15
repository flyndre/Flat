<script setup lang="ts">
import { mapCenterWithDefaults } from '@/util/googleMapsUtils';
import { mdiCrosshairsGps } from '@mdi/js';
import { useGeolocation } from '@vueuse/core';
import { onMounted } from 'vue';
import MdiIcon from '../icons/MdiIcon.vue';
import Button from 'primevue/button';

const props = withDefaults(
    defineProps<{
        /** Whether the `locateMeHandler` should be called on mounted. @default true */
        initialPan?: boolean;
        /** A function to handle when the button is clicked. */
        locateMeHandler: (clientPos: google.maps.LatLngLiteral) => any;
    }>(),
    {
        initialPan: true,
    }
);

const { coords: clientPos, error: clientPosError } = useGeolocation();
const clientPosWithFallback = mapCenterWithDefaults(clientPos, {
    lat: 0,
    lng: 0,
});

function locateMe() {
    props.locateMeHandler(clientPosWithFallback.value);
}

onMounted(() => {
    if (clientPosError === null) {
        locateMe();
    }
});
</script>

<template>
    <Button
        severity="secondary"
        :disabled="clientPosError !== null"
        @click="locateMe"
    >
        <template #icon>
            <MdiIcon :icon="mdiCrosshairsGps" />
        </template>
    </Button>
</template>
