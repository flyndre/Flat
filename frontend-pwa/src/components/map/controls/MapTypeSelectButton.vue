<script setup lang="ts">
import SelectButton from 'primevue/selectbutton';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import { MapType } from '@/types/map/MapType';

export type MapTypeOption = {
    value: MapType;
    icon: string;
};

const selectedMapType = defineModel<MapType>({
    required: true,
});

defineProps<{
    options: MapTypeOption[]
}>();
</script>

<template>
    <SelectButton
        v-model="selectedMapType"
        :options
        data-key="value"
        option-value="value"
        :allow-empty="false"
        :pt="{
            button: {
                class: 'h-9 w-auto grow flex flex-row justify-center',
            },
            root: {
                class: 'w-auto flex flex-row grow justify-stretch',
            },
        }"
    >
        <template #option="slotProps">
            <MdiIcon :icon="slotProps.option.icon" />
        </template>
    </SelectButton>
</template>
