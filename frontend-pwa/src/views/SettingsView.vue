<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import AboutSetting from '@/components/settings/AboutSetting.vue';
import HandednessSetting from '@/components/settings/HandednessSetting.vue';
import HomeLocationSetting from '@/components/settings/HomeLocationSetting.vue';
import LocaleSetting from '@/components/settings/LocaleSetting.vue';
import ThemeSetting from '@/components/settings/ThemeSetting.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import {
    mdiArrowLeft,
    mdiHandClap,
    mdiHomeMapMarker,
    mdiInformation,
    mdiPalette,
    mdiTranslate,
} from '@mdi/js';
import Accordion from 'primevue/accordion';
import AccordionTab from 'primevue/accordiontab';
import Button from 'primevue/button';
import Card from 'primevue/card';

const settings = [
    {
        messageCode: 'settings.theme',
        icon: mdiPalette,
        component: ThemeSetting,
    },
    {
        messageCode: 'settings.handedness',
        icon: mdiHandClap,
        component: HandednessSetting,
    },
    {
        messageCode: 'settings.locale',
        icon: mdiTranslate,
        component: LocaleSetting,
    },
    {
        messageCode: 'settings.home_location',
        icon: mdiHomeMapMarker,
        component: HomeLocationSetting,
    },
    {
        messageCode: 'settings.about',
        icon: mdiInformation,
        component: AboutSetting,
    },
];
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'home' }">
                <Button :label="$t('universal.back')" severity="secondary" text>
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> {{ $t('settings.title') }} </template>
        <template #default>
            <Card
                class="mb-2.5 overflow-hidden"
                :pt="{ body: { class: 'p-0 overflow-hidden' } }"
            >
                <template #content>
                    <Accordion :multiple="true" :active-index="[0, 1]">
                        <AccordionTab
                            v-for="setting of settings"
                            :key="setting.messageCode"
                            :pt="{ headerAction: { class: 'bg-transparent' } }"
                        >
                            <template #header>
                                <span class="mr-auto">
                                    {{ $t(setting.messageCode) }}
                                </span>
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
