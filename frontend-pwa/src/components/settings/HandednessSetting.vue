<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import { useSettings } from '@/plugins/SettingsPlugin';
import { mdiHandBackLeft, mdiHandBackRight } from '@mdi/js';
import SelectButton from 'primevue/selectbutton';

const { settings } = useSettings();
const options: {
    messageCode: string;
    value: 'left' | 'right';
    icon: string;
}[] = [
    {
        messageCode: 'components.handedness_setting.left',
        value: 'left',
        icon: mdiHandBackLeft,
    },
    {
        messageCode: 'components.handedness_setting.right',
        value: 'right',
        icon: mdiHandBackRight,
    },
];
</script>

<template>
    <SelectButton
        class="flex w-full flex-row"
        v-model="settings.handedness"
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
                    {{ $t(slotProps.option.messageCode) }}
                </span>
            </div>
        </template>
    </SelectButton>
</template>
