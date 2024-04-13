<script setup lang="ts">
import Card from 'primevue/card';
import MapWithControls from '@/components/map/MapWithControls.vue';
import { ref } from 'vue';
import { GeometryObject } from 'geojson';
import { IdentifyableTypedOverlay } from '@/types/map/IdentifyableTypedOverlay';
import { Overlay } from '@/types/map/Overlay';

const areas = ref<Overlay[]>([]);
</script>

<template>
    <Card>
        <template #content>
            <MapWithControls v-model:shapes="areas" />
        </template>
    </Card>
    {{ areas.map((s) => s.type) }}
</template>
