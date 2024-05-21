<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { mapCenterWithDefaults } from '@/util/googleMapsUtils';
import { mdiCog, mdiImport, mdiMapMarkerPath } from '@mdi/js';
import { useGeolocation, useThrottle } from '@vueuse/core';
import Button from 'primevue/button';
import brandingSrc from '@/assets/images/branding.webp?url';
import { isOnMobile } from '@/util/mobileDetection';
import Slider from 'primevue/slider';
import { ref } from 'vue';
import { useSettings } from '@/plugins/SettingsPlugin';
import { computed } from 'vue';
import CinematicMap from '@/components/map/CinematicMap.vue';

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
                <Button :label="$t('home.join')">
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiImport" />
                    </template>
                </Button>
            </router-link>
            <router-link :to="{ name: 'presets' }">
                <Button :label="$t('home.presets')" severity="secondary">
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiMapMarkerPath" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #background>
            <CinematicMap :center="mapCenter" :zoom="mapZoomReal" />
        </template>
        <template #default>
            <div
                class="grow flex flex-col justify-center items-center select-none"
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
