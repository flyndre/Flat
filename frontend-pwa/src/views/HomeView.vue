<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import TextButtonIcon from '@/components/icons/TextButtonIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { safeMapCenterFromGeolocationCoords } from '@/util/googleMapsUtils';
import {
    mdiCog,
    mdiImport,
    mdiInformationOutline,
    mdiMapMarkerPath,
} from '@mdi/js';
import { useGeolocation } from '@vueuse/core';
import Button from 'primevue/button';
import { GoogleMap } from 'vue3-google-map';
import brandingSrc from '@/assets/images/branding.webp?url';

const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
const mapCenter = safeMapCenterFromGeolocationCoords(useGeolocation().coords);
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
                class="w-full h-full scale-105 blur-sm"
                :api-key
                :zoom="15"
                :center="mapCenter"
                :disable-default-ui="true"
            />
        </template>
        <template #default>
            <div
                class="h-full w-full flex flex-col justify-center items-center"
            >
                <img class="object-contain w-full" :src="brandingSrc" />
            </div>
        </template>
    </DefaultLayout>
</template>
