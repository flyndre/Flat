<script setup lang="ts">
import SelectButton from 'primevue/selectbutton';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import { mdiCircle } from '@mdi/js';
const selectedColor = defineModel<string>({
    required: false,
    default: '#1E90FF',
});
const colorOptions: string[] = [
    '#1E90FF',
    '#FF1493',
    '#32CD32',
    '#FF8C00',
    '#4B0082',
];
</script>

<template>
    <SelectButton
        v-model="selectedColor"
        :options="colorOptions"
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
            <MdiIcon :icon="mdiCircle" :style="{ color: slotProps.option }" />
        </template>
    </SelectButton>
</template>
