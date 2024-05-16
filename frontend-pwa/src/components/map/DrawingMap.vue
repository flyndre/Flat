<script setup lang="ts">
import { getShapeListBounds } from '@/util/googleMapsUtils';
import LocateMeButton from './controls/LocateMeButton.vue';
import LocateShapesButton from './controls/LocateShapesButton.vue';
import CustomMap from './CustomMap.vue';
import { computed, onMounted, ref } from 'vue';
import { collectionDB } from '@/data/collections';
import { useTrackingService } from '@/service/trackingService';
import { mdiMap, mdiPalette, mdiTextureBox } from '@mdi/js';
import ShapesList from './controls/ShapesList.vue';
import MdiTextButtonIcon from '../icons/MdiTextButtonIcon.vue';
import TabPanel from 'primevue/tabpanel';
import TabView from 'primevue/tabview';
import { isOnMobile } from '@/util/mobileDetection';
import LocationSearchDialog from './controls/LocationSearchDialog.vue';
import MapTypeSelectButton from './controls/MapTypeSelectButton.vue';
import DrawShapeButton from './controls/DrawShapeButton.vue';
import DeleteShapeButton from './controls/DeleteShapeButton.vue';
import ShapeColorSelectButton from './controls/ShapeColorSelectButton.vue';

const collection = ref(undefined);
onMounted(async () => {
    try {
        const storedCollection = await collectionDB.get(
            '4fd5473d-0be7-4d7c-b923-546c32e0a09c'
        );
        collection.value = storedCollection;
    } catch (error) {
        console.log('propbel');
    }
});

const { coords } = useTrackingService();
const clientPos = computed(() => ({
    lat: coords.value.latitude,
    lng: coords.value.longitude,
}));

const divisions = ref([]);
const mapType = ref();
const selectedTool = ref();
const selectedColor = ref();
</script>

<template>
    <CustomMap
        controls="drawing"
        :map-type
        v-model:divisions="divisions"
        :client-pos
    >
        <template #default="slotProps">
            <TabView
                :pt="{
                    root: {
                        class:
                            'flex ' +
                            (isOnMobile ? 'flex-col-reverse' : 'flex-col'),
                    },
                    nav: {
                        class: [isOnMobile ? 'mt-2' : 'mb-2'],
                    },
                    inkbar: { class: 'rounded-t h-1' },
                    panelContainer: { class: 'p-0' },
                }"
            >
                <TabPanel>
                    <template #header>
                        <div class="flex justify-center items-center">
                            <MdiTextButtonIcon :icon="mdiMap" />
                            Map
                        </div>
                    </template>
                    <div
                        class="flex flex-row gap-2 items-center justify-stretch flex-wrap"
                    >
                        <div
                            class="flex flex-row gap-2 grow text-nowrap basis-7/12"
                        >
                            <LocateMeButton
                                :client-pos
                                :locate-me-handler="
                                    (r) => {
                                        slotProps.panMapToPos(r);
                                    }
                                "
                            />
                            <LocateShapesButton
                                :shapes-present="divisions?.length > 0"
                                :locate-shapes-handler="
                                    () => slotProps.panMapToDivisions(divisions)
                                "
                            />
                            <!-- <LocationSearchDialog
                                :places-service
                                :select-result-callback="
                                    (r) => panMapToPos(r.geometry?.location)
                                "
                            /> -->
                        </div>
                        <MapTypeSelectButton
                            class="basis-1/12"
                            v-model="mapType"
                        />
                    </div>
                </TabPanel>
                <TabPanel>
                    <template #header>
                        <div class="flex justify-center items-center">
                            <MdiTextButtonIcon :icon="mdiPalette" />
                            Tools
                        </div>
                    </template>
                    <div
                        class="flex flex-row gap-2 items-center justify-stretch flex-wrap"
                    >
                        <div
                            class="flex flex-row gap-2 items-center justify-stretch flex-nowrap grow basis-0"
                        >
                            <DrawShapeButton v-model="selectedTool" />
                            <DeleteShapeButton
                                :shape-selected="
                                    slotProps.divisionSelected.value
                                "
                                :delete-shape-handler="
                                    slotProps.deleteSelectedDivision
                                "
                            />
                        </div>
                        <ShapeColorSelectButton
                            class="basis-52 min-w-[240px]"
                            v-model="selectedColor"
                        />
                    </div>
                </TabPanel>
                <TabPanel>
                    <template #header>
                        <div class="flex justify-center items-center">
                            <MdiTextButtonIcon :icon="mdiTextureBox" />
                            Divisions
                        </div>
                    </template>
                    {{ divisions }}
                    <!-- <ShapesList
                        :shapes="divisions"
                        :center-shape-hook="centerShape"
                        :delete-shape-hook="deleteShape"
                        :delete-all-shapes-hook="deleteAllShapes"
                        :set-shape-name-hook="setShapeName"
                    /> -->
                </TabPanel>
            </TabView>
        </template>
    </CustomMap>
</template>
