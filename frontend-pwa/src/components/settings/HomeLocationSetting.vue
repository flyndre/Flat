<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import { useSettings } from '@/plugins/SettingsPlugin';
import { mdiMapMarker, mdiPencil } from '@mdi/js';
import SelectButton from 'primevue/selectbutton';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import InputNumber from 'primevue/inputnumber';

const { settings } = useSettings();
const options: {
    label: string;
    value: boolean;
    icon: string;
}[] = [
    {
        label: 'Live',
        value: true,
        icon: mdiMapMarker,
    },
    {
        label: 'Custom',
        value: false,
        icon: mdiPencil,
    },
];
</script>

<template>
    <SelectButton
        class="flex w-full flex-row"
        v-model="settings.homeLive"
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
    <div class="flex flex-row gap-2.5 max-[400px]:flex-wrap mt-2.5">
        <InputGroup>
            <InputGroupAddon class="w-12"> Lat </InputGroupAddon>
            <InputNumber
                locale="en-US"
                :min-fraction-digits="0"
                :max-fraction-digits="6"
                :format="false"
                :disabled="settings.homeLive === true"
                v-model="settings.homeLatitude"
            />
        </InputGroup>
        <InputGroup>
            <InputGroupAddon class="w-12"> Lon </InputGroupAddon>
            <InputNumber
                locale="en-US"
                :min-fraction-digits="0"
                :max-fraction-digits="6"
                :format="false"
                :disabled="settings.homeLive === true"
                v-model="settings.homeLongitude"
            />
        </InputGroup>
    </div>
</template>
