<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import TextButtonIcon from '@/components/icons/TextButtonIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { mapCenterWithDefaults } from '@/util/googleMapsUtils';
import { mdiCog, mdiImport, mdiMapMarkerPath } from '@mdi/js';
import { useGeolocation, useThrottle } from '@vueuse/core';
import Button from 'primevue/button';
import { GoogleMap } from 'vue3-google-map';
import brandingSrc from '@/assets/images/branding.webp?url';
import { isOnMobile } from '@/util/mobileDetection';
import Slider from 'primevue/slider';
import { ref } from 'vue';
import { useSettings } from '@/plugins/SettingsPlugin';
import { computed } from 'vue';

const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
const { settings } = useSettings();
const homeCoordsDefaults = computed(() => ({
    lat: settings.value.homeLatitude,
    lng: settings.value.homeLongitude,
}));
const mapCenter = settings.value.homeLive
    ? mapCenterWithDefaults(useGeolocation().coords, homeCoordsDefaults.value)
    : homeCoordsDefaults.value;
const mapZoomSlider = ref(15);
const mapZoomReal = useThrottle(mapZoomSlider, 25);
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'settings' }">
                <Button severity="secondary">
                    <template #icon>
                        <MdiIcon :icon="mdiCog" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #action-right>
            <router-link :to="{ name: 'scan' }">
                <Button label="Join a Collection">
                    <template #icon>
                        <TextButtonIcon :icon="mdiImport" />
                    </template>
                </Button>
            </router-link>
            <router-link :to="{ name: 'presets' }">
                <Button label="My Collections" severity="secondary">
                    <template #icon>
                        <TextButtonIcon :icon="mdiMapMarkerPath" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #background>
            <GoogleMap
                class="min-w-[150vmax] min-h-[150vmax] blur-[1px] bg-yellow-50 c-animate-areal"
                :api-key
                :zoom="mapZoomReal"
                :center="mapCenter"
                :map-type-id="'satellite'"
                :disable-default-ui="true"
            />
            <div
                class="!z-10 fixed -top-[10%] -left-[10%] -right-[10%] -bottom-[10%] backdrop-blur c-tilt-shift-filter"
            ></div>
        </template>
        <template #default>
            <div
                class="h-full w-full flex flex-col justify-center items-center select-none"
                :class="{ 'pb-[10vh]': !isOnMobile }"
            >
                <!-- ⭕ -->
                <img class="object-contain w-full" :src="brandingSrc" />
                <Slider
                    class="cursor-pointer"
                    :class="[
                        !isOnMobile
                            ? 'w-1/2'
                            : `fixed h-1/2 ${settings.handedness === 'right' ? 'right-8' : 'left-8'}`,
                    ]"
                    v-model="mapZoomSlider"
                    :orientation="isOnMobile ? 'vertical' : 'horizontal'"
                    :step="0.01"
                    :min="4"
                    :max="20"
                />
            </div>
            <div id="clouds" class="animate-areal" />
        </template>
    </DefaultLayout>
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
