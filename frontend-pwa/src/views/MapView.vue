<script setup lang="ts">
import Card from 'primevue/card';
import MapWithControls from '@/components/map/MapWithControls.vue';
import { ref, watch } from 'vue';
import { GeometryObject } from 'geojson';
import { IdentifyableTypedOverlay } from '@/types/map/IdentifyableTypedOverlay';
import { Overlay } from '@/types/map/Overlay';
import { TypedOverlay } from '@/types/map/TypedOverlay';
import { computed } from 'vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import TextButtonIcon from '@/components/icons/TextButtonIcon.vue';
import Button from 'primevue/button';
import { mdiArrowLeft, mdiCheck } from '@mdi/js';

const areas = ref<TypedOverlay[]>([]);

const props = withDefaults(
    defineProps<{
        edit?: boolean;
        id?: number;
    }>(),
    {
        edit: false,
        id: undefined,
    }
);

const addShape = () => {
    areas.value.push({
        overlay: new google.maps.Polygon({
            paths: [
                { lat: 48.384521, lng: 8.582583 },
                { lat: 48.383178, lng: 8.583846 },
                { lat: 48.383348, lng: 8.580421 },
            ],
            strokeColor: '#FF0000',
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: '#FF0000',
            fillOpacity: 0.35,
        }),
        type: google.maps.drawing.OverlayType.POLYGON,
    });
};

const shapeHtml = computed(() =>
    areas.value
        .map(
            (s) =>
                /*html*/ `<span style="color: ${s.overlay.get('fillColor') || s.overlay.get('strokeColor')}" >${s.type}</span>`
        )
        .join('&nbsp;')
);
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link
                :to="{
                    name: edit ? 'edit' : 'create',
                    params: { id: props.id },
                }"
            >
                <Button label="Back" severity="secondary" text>
                    <template #icon>
                        <TextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> Edit Map </template>
        <template #action-right>
            <div class="flex flex-row gap-2">
                <Button label="Save" severity="primary">
                    <template #icon>
                        <TextButtonIcon :icon="mdiCheck" />
                    </template>
                </Button>
            </div>
        </template>
        <template #default>
            <MapWithControls v-model:shapes="areas" />
        </template>
    </DefaultLayout>
    <!-- <div v-html="shapeHtml"></div>
    <button @click="addShape">Add Shape</button> -->
</template>
