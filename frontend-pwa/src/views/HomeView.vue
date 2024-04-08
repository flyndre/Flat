<script setup lang="ts">
import Button from "primevue/button";
import Card from "primevue/card";
import AppBar from "@/components/AppBar.vue";
import MdiIcon from "@/components/MdiIcon.vue";
import { mdiImport, mdiMapMarkerPath } from "@mdi/js";
</script>

<template>
    <div class="h-screen flex flex-col justify-stretch">
        <AppBar>
            <template #action-left>
                <router-link :to="{ name: 'presets' }">
                    <Button label="My Collections" text>
                        <template #icon>
                            <MdiIcon class="mr-2.5" :icon="mdiMapMarkerPath" />
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
            </template>
        </AppBar>
        <div
            class="w-full flex-grow bg-white flex flex-col items-center justify-center text-gray-500"
        >
            Map Placeholder
        </div>
        <!-- <Card :pt="{ footer: { class: 'flex flex-row gap-2' } }">
        <template #title> Flat </template>
        <template #subtitle> Fleet Live Area Tracking </template>
        <template #content>
            The modern cross-platform route tracking app to orchestrate
            volunteer collection campaigns in your neighborhood.
        </template>
        <template #footer>
            <a href="https://github.com/flyndre/Flat">
                <Button label="Repository" />
            </a>
            <a href="https://flyndre.github.io">
                <Button text label="Team Website" />
            </a>
        </template>
    </Card> -->
    </div>
</template>
