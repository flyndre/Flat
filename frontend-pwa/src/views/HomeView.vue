<script setup lang="ts">
import brandingSrc from '@/assets/images/branding.webp?url';
import DemoInfo from '@/components/home/DemoInfo.vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import CinematicMap from '@/components/map/CinematicMap.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { useSettings } from '@/plugins/SettingsPlugin';
import { useSanitizedGeolocation } from '@/util/geoLocationUtils';
import { isOnMobile } from '@/util/mobileDetection';
import { mdiCog, mdiImport, mdiMapMarkerPath } from '@mdi/js';
import { refThrottled } from '@vueuse/core';
import Button from 'primevue/button';
import Slider from 'primevue/slider';
import { computed, ref } from 'vue';
import backgroundImageSrc from '@/assets/images/background-wireframe.webp?url';
import { useTheme } from '@/plugins/ThemePlugin';

const mode = import.meta.env.MODE;

const { settings } = useSettings();
const { activeTheme } = useTheme();
const { coords } = useSanitizedGeolocation();
const mapCenter = computed(() => {
    switch (settings.value.homePosition) {
        case 'off':
            return undefined;
    
        case 'live':
            if (coords.value !== undefined) {
                return [coords.value.longitude, coords.value.latitude];
            }
            return undefined;
        
        case 'static':
            return [
            settings.value.homeLongitude,
            settings.value.homeLatitude,
        ];
    }
});
const mapZoomSlider = ref(15.5);
const mapZoomReal = refThrottled(mapZoomSlider, 25);
</script>

<template>
    <DefaultLayout :backdrop="false">
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
            <div
                class="fixed -top-[10%] -left-[10%] -right-[10%] -bottom-[10%]"
                :class="[ activeTheme === 'light' ? 'opacity-10' : 'opacity-15' ]"
                :style="{ backgroundSize: '50rem', backgroundImage: `url('${backgroundImageSrc}')`  }"
            ></div>
            <CinematicMap v-if="mapCenter !== undefined" :center="mapCenter" :zoom="mapZoomReal" />
        </template>
        <template #default>
            <div
                class="grow flex flex-col justify-center items-center select-none"
                :class="{ 'pb-[10vh]': !isOnMobile }"
            >
                <img class="object-contain w-full" :src="brandingSrc" />
                <Slider
                    v-if="mapCenter !== undefined"
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
                    :max="16.5"
                />
                <DemoInfo v-if="mode === 'demo'" />
            </div>
        </template>
    </DefaultLayout>
</template>
