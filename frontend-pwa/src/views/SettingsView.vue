<script setup lang="ts">
import TextButtonIcon from '@/components/icons/TextButtonIcon.vue';
import ThemeSetting from '@/components/settings/ThemeSetting.vue';
import HomeLocationSetting from '@/components/settings/HomeLocationSetting.vue';
import HandednessSetting from '@/components/settings/HandednessSetting.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { mdiArrowLeft } from '@mdi/js';
import Button from 'primevue/button';
import Card from 'primevue/card';

const settings = [
    {
        label: 'Theme',
        component: ThemeSetting,
    },
    {
        label: 'Home Screen Location',
        component: HomeLocationSetting,
    },
    {
        label: 'Handedness',
        component: HandednessSetting,
    },
];
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
        <template #title> Settings </template>
        <template #default>
            <Card v-for="setting of settings" class="mb-2.5">
                <template #title> {{ setting.label }} </template>
                <template #content>
                    <component :is="setting.component" />
                </template>
            </Card>

            <Card :pt="{ footer: { class: 'flex flex-row gap-2' } }">
                <template #title> About Flat </template>
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
