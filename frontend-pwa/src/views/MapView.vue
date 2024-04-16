<script setup lang="ts">
import MapWithControls from '@/components/map/MapWithControls.vue';
import { ref } from 'vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import TextButtonIcon from '@/components/icons/TextButtonIcon.vue';
import Button from 'primevue/button';
import { mdiArrowLeft, mdiCheck, mdiDuck } from '@mdi/js';
import { v4 as uuidv4 } from 'uuid';
import { Division } from '@/types/Division';

const areas = ref<Division[]>([]);

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
        area: {
            type: 'MultiPolygon',
            coordinates: [
                [
                    [
                        [48.384521, 8.582583],
                        [48.383178, 8.583846],
                        [48.383348, 8.580421],
                    ],
                ],
            ],
        },
        id: uuidv4(),
        name: 'Mein erstes Polygon 😊',
        color: 'lime',
    });
};

function _saveAreas() {
    console.log(areas.value);
}
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
            <Button label="Save" severity="primary" @click="_saveAreas">
                <template #icon>
                    <TextButtonIcon :icon="mdiCheck" />
                </template>
            </Button>
        </template>
        <template #default>
            <MapWithControls v-model:areas="areas" />
        </template>
    </DefaultLayout>
    <!-- <div v-html="shapeHtml"></div>
    <button @click="addShape">Add Shape</button> -->
</template>
