<script setup lang="ts">
import TextButtonIcon from "@/components/icons/TextButtonIcon.vue";
import MdiIcon from "@/components/icons/MdiIcon.vue";
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
            <router-link :to="{ name: 'scan' }">
                <Button label="Join a Collection">
                    <template #icon>
                        <TextButtonIcon :icon="mdiImport" />
                    </template>
                </Button>
            </router-link>
            <router-link :to="{ name: 'presets' }">
                <Button label="My Collections" text>
                    <template #icon>
                        <TextButtonIcon :icon="mdiMapMarkerPath" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #background>
            <div class="w-full h-full bg-gray-200 flex flex-col items-center justify-center text-gray-500">
                Map Placeholder
            </div>
        </template>
    </DefaultLayout>
</template>
