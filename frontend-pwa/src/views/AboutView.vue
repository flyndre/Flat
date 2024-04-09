<script setup lang="ts">
import TextButtonIcon from "@/components/icons/TextButtonIcon.vue";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import { mdiArrowLeft } from "@mdi/js";
import Button from "primevue/button";
import Card from "primevue/card";
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'home' }">
                <Button label="Back" severity="secondary" text>
                    <template #icon>
                        <TextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> About </template>
        <template #default>
            <Card :pt="{ footer: { class: 'flex flex-row gap-2' } }">
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
            </Card>
        </template>
    </DefaultLayout>
</template>
