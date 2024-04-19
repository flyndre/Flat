<script setup lang="ts">
import SelectButton from 'primevue/selectbutton';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import { mdiHandBackRightOutline, mdiShapePolygonPlus } from '@mdi/js';

const selectedToolType = defineModel<google.maps.drawing.OverlayType>({
    required: false,
    default: null,
});

type ToolTypeOption = {
    value: `${google.maps.drawing.OverlayType}`;
    icon: string;
};

const toolTypes: ToolTypeOption[] = [
    {
        value: null,
        icon: mdiHandBackRightOutline,
    },
    {
        value: 'polygon',
        icon: mdiShapePolygonPlus,
    },
];
</script>

<template>
    <SelectButton
        v-model="selectedToolType"
        :options="toolTypes"
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
