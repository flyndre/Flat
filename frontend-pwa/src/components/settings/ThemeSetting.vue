<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import { useSettings } from '@/plugins/SettingsPlugin';
import { isOnMobile } from '@/util/mobileDetection';
import {
    mdiBrightness2,
    mdiCellphone,
    mdiLaptop,
    mdiWhiteBalanceSunny,
} from '@mdi/js';
import { ColorSchemeType } from '@vueuse/core';
import SelectButton from 'primevue/selectbutton';

const { settings } = useSettings();
const options: {
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
        icon: isOnMobile.value ? mdiCellphone : mdiLaptop,
    },
    {
        label: 'Dark',
        value: 'dark',
        icon: mdiBrightness2,
    },
];
</script>

<template>
    <SelectButton
        class="flex w-full flex-row"
        v-model="settings.theme"
        :options="options"
        :option-value="(o) => o.value"
        :allow-empty="false"
        :pt="{ button: { class: 'w-full' } }"
    >
        <template #option="slotProps">
            <div
                class="flex flex-row justify-center items-center flex-nowrap w-full gap-3 min-h-6"
            >
                <MdiIcon :icon="slotProps.option.icon" />
                <span
                    class="max-[400px]:hidden text-ellipsis overflow-hidden z-10"
                >
                    {{ slotProps.option.label }}
                </span>
            </div>
        </template>
    </SelectButton>
</template>
