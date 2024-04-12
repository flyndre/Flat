<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import TextButtonIcon from '@/components/icons/TextButtonIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { isOnMobile } from '@/util/mobileDetection';
import {
    mdiArrowLeft,
    mdiBrightness2,
    mdiCellphone,
    mdiLaptop,
    mdiWhiteBalanceSunny,
} from '@mdi/js';
import { ColorSchemeType } from '@vueuse/core';
import Button from 'primevue/button';
import Card from 'primevue/card';
import SelectButton from 'primevue/selectbutton';
import { ref } from 'vue';

const selectedTheme = ref('no-preference');
const themeOptions: {
    label: string;
    value: ColorSchemeType;
    icon: string;
}[] = [
    {
        label: 'Light',
        value: 'light',
        icon: mdiWhiteBalanceSunny,
    },
    {
        label: 'Device',
        value: 'no-preference',
        icon: isOnMobile ? mdiCellphone : mdiLaptop,
    },
    {
        label: 'Dark',
        value: 'dark',
        icon: mdiBrightness2,
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
            <Card class="mb-2.5">
                <template #content>
                    <SelectButton
                        class="flex w-full flex-row"
                        v-model="selectedTheme"
                        :options="themeOptions"
                        :option-value="(o) => o.value"
                        :allow-empty="false"
                        :pt="{ button: { class: 'w-full' } }"
                    >
                        <template #option="slotProps">
                            <div
                                class="flex flex-row justify-center items-center flex-nowrap w-full gap-3"
                            >
                                <MdiIcon :icon="slotProps.option.icon" />
                                <span
                                    class="text-ellipsis overflow-hidden z-10"
                                >
                                    {{ slotProps.option.label }}
                                </span>
                            </div>
                        </template>
                    </SelectButton>
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
