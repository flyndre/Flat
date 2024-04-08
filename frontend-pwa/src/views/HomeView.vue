<script setup lang="ts">
import MdiIcon from "@/components/MdiIcon.vue";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import { mdiImport, mdiInformationOutline, mdiMapMarkerPath } from "@mdi/js";
import Button from "primevue/button";
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'about' }">
                <Button severity="secondary" text>
                    <template #icon>
                        <MdiIcon :icon="mdiInformationOutline" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #action-right>
            <router-link :to="{ name: 'join' }">
                <Button label="Join a Collection">
                    <template #icon>
                        <MdiIcon class="mr-2.5" :icon="mdiImport" />
                    </template>
                </Button>
            </router-link>
            <router-link :to="{ name: 'presets' }">
                <Button label="My Collections" text>
                    <template #icon>
                        <MdiIcon class="mr-2.5" :icon="mdiMapMarkerPath" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #background>
            <div
                class="w-full h-full bg-gray-200 flex flex-col items-center justify-center text-gray-500"
            >
                Map Placeholder
            </div>
        </template>
    </DefaultLayout>
</template>
