<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import AboutSetting from '@/components/settings/AboutSetting.vue';
import HandednessSetting from '@/components/settings/HandednessSetting.vue';
import HomeLocationSetting from '@/components/settings/HomeLocationSetting.vue';
import ThemeSetting from '@/components/settings/ThemeSetting.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import {
    mdiArrowLeft,
    mdiHandClap,
    mdiHomeMapMarker,
    mdiInformation,
    mdiPalette,
} from '@mdi/js';
import Accordion from 'primevue/accordion';
import AccordionTab from 'primevue/accordiontab';
import Button from 'primevue/button';
import Card from 'primevue/card';

const settings = [
    {
        label: 'Theme',
        icon: mdiPalette,
        component: ThemeSetting,
    },
    {
        label: 'Handedness',
        icon: mdiHandClap,
        component: HandednessSetting,
    },
    {
        label: 'Home Screen Location',
        icon: mdiHomeMapMarker,
        component: HomeLocationSetting,
    },
    {
        label: 'About',
        icon: mdiInformation,
        component: AboutSetting,
    },
];
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'home' }">
                <Button label="Back" severity="secondary" text>
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> Settings </template>
        <template #default>
            <Card
                class="mb-2.5 overflow-hidden"
                :pt="{ body: { class: 'p-0 overflow-hidden' } }"
            >
                <template #content>
                    <Accordion :multiple="true" :active-index="[0, 1]">
                        <AccordionTab
                            v-for="setting of settings"
                            :key="setting.label"
                            :pt="{ headerAction: { class: 'bg-transparent' } }"
                        >
                            <template #header>
                                <span class="mr-auto">{{ setting.label }}</span>
                                <MdiIcon class="mr-3" :icon="setting.icon" />
                            </template>
                            <component :is="setting.component" />
                        </AccordionTab>
                    </Accordion>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
