<script setup lang="ts">
import {
    GOOGLE_MAPS_API_KEY,
    GOOGLE_MAPS_API_LIBRARIES,
} from '@/data/constants';
import { GoogleMap } from 'vue3-google-map';

const apiKey = GOOGLE_MAPS_API_KEY;
const libraries = GOOGLE_MAPS_API_LIBRARIES;

const props = defineProps<{
    zoom: number;
    center: google.maps.LatLng | google.maps.LatLngLiteral;
}>();
</script>

<template>
    <GoogleMap
        class="min-w-[150vmax] min-h-[150vmax] blur-[1px] bg-yellow-50 c-animate-areal"
        :api-key
        :libraries
        :zoom
        :center
        :map-type-id="'satellite'"
        :disable-default-ui="true"
    />
    <div
        class="!z-10 fixed -top-[10%] -left-[10%] -right-[10%] -bottom-[10%] backdrop-blur c-tilt-shift-filter"
    ></div>
</template>

<style scoped>
.c-animate-areal {
    --transform-base: scale(2) perspective(35rem) rotateX(45deg)
        translateZ(1.8vh);
    animation: c-areal-animation calc(2 * 60s) linear infinite;
}

@keyframes c-areal-animation {
    from {
        transform: var(--transform-base) rotateZ(0deg);
    }
    to {
        transform: var(--transform-base) rotateZ(360deg);
    }
}

.c-tilt-shift-filter {
    background: linear-gradient(
        rgba(0, 133, 221, 0.1) 0,
        rgba(0, 109, 182, 0.05) 30%,
        transparent 45%,
        transparent 60%,
        rgba(71, 0, 88, 0.1) 90%,
        rgba(51, 0, 40, 0.2) 100%
    );
    mask: linear-gradient(
            black 0,
            rgba(0, 0, 0, 0.9) 35%,
            rgba(0, 0, 0, 0.5) 40%,
            transparent 45%,
            transparent 60%,
            rgba(0, 0, 0, 0.5) 62%,
            rgba(0, 0, 0, 0.9) 75%,
            black 100%
        )
        alpha;
}
</style>
