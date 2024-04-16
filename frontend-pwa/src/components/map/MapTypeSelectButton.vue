<script setup lang="ts">
import SelectButton from 'primevue/selectbutton';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import { mdiRoadVariant, mdiTerrain, mdiEarth, mdiEarthPlus } from '@mdi/js';
const selectedMapType = defineModel<google.maps.MapTypeId>({
    required: false,
    default: 'roadmap' as google.maps.MapTypeId,
});
type MapTypeOption = {
    value: `${google.maps.MapTypeId}`;
    icon: string;
};
const mapTypeOptions: MapTypeOption[] = [
    {
        value: 'roadmap',
        icon: mdiRoadVariant,
    },
    {
        value: 'terrain',
        icon: mdiTerrain,
    },
    {
        value: 'satellite',
        icon: mdiEarth,
    },
    {
        value: 'hybrid',
        icon: mdiEarthPlus,
    },
];
</script>

<template>
    <SelectButton
        v-model="selectedMapType"
        :options="mapTypeOptions"
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
