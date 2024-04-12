<script setup lang="ts">
import { computed, ref } from 'vue';
import { useGeolocation } from '@vueuse/core';
import Button from 'primevue/button';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import { mdiDeleteSweep, mdiDelete, mdiCrosshairsGps } from '@mdi/js';
import MapTypeSelectButton from '@/components/map/MapTypeSelectButton.vue';
import DrawingToolSelectButton from '@/components/map/DrawingToolSelectButton.vue';
import ShapeColorSelectButton from './ShapeColorSelectButton.vue';
import { nextTick } from 'vue';
import { GoogleMap } from 'vue3-google-map';
import { watch } from 'vue';

const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
const mapComponentRef = ref<InstanceType<typeof GoogleMap> | null>();
const mapReady = computed(() => mapComponentRef.value?.ready);
const map = computed(() => mapComponentRef.value?.map);

watch(mapReady, (v) => {
    if (!v) return;
    initialize();
});

const shapeList = ref<IdentifyableTypedShape[]>([]);
const mapZoom = 15;

const { coords: clientPos, error: clientPosError } = useGeolocation();
const mapCenter = computed(() => ({
    lat: clientPos.value.latitude,
    lng: clientPos.value.longitude,
}));

function centerMap() {
    try {
        map.value?.panTo({
            lat: clientPos.value.latitude,
            lng: clientPos.value.longitude,
        });
    } catch (e) {}
    map.value?.setZoom(mapZoom);
}

/**
 * A setup for a Google Maps Map with shape tracking.
 * @see https://stackoverflow.com/a/12006751/11793652
 */
export type TypedShape = {
    overlay:
        | google.maps.Polygon
        | google.maps.Polyline
        | google.maps.Rectangle
        | google.maps.Circle;
    type: google.maps.drawing.OverlayType;
};
export type IdentifyableTypedShape = TypedShape & { id: string };

// const selectedMapType = ref<>()
const drawingManager = ref<google.maps.drawing.DrawingManager>();
const selectedShapeId = ref<string>();
const selectedShape = computed(() =>
    shapeList.value.find((s) => s.id === selectedShapeId.value)
);
const colors = ref(['#1E90FF', '#FF1493', '#32CD32', '#FF8C00', '#4B0082']);
const selectedColor = ref<string>();
const shapeOptions = {
    strokeWeight: 0,
    fillOpacity: 0.45,
    editable: true,
    draggable: true,
};
const lineOptions = {
    ...shapeOptions,
    strokeWeight: 4,
    strokeOpacity: 0.45,
};

function clearSelection() {
    if (!selectedShapeEmpty.value) {
        selectedShape.value.overlay.setEditable(false);
        selectedShapeId.value = null;
    }
}

function setSelection(shapeId: string) {
    clearSelection();
    selectedShapeId.value = shapeId;
    nextTick(() => {
        selectedShape.value?.overlay?.setEditable(true);
        selectColor(
            selectedShape.value?.overlay?.get('fillColor') ||
                selectedShape.value?.overlay?.get('strokeColor')
        );
    });
}

function deleteSelectedShape() {
    if (selectedShape.value !== undefined) {
        selectedShape.value.overlay.setMap(null);
        shapeList.value.splice(
            shapeList.value.findIndex(
                (oce) => oce.id === selectedShape.value.id
            )
        );
        selectedShapeId.value = null;
    }
}

function deleteAllShapes() {
    selectedShapeId.value = null;
    shapeList.value.forEach((s) => s.overlay.setMap(null));
    shapeList.value = [];
}

function selectColor(color: string) {
    selectedColor.value = color;

    // Retrieves the current options from the drawing manager and replaces the
    // stroke or fill color as appropriate.
    const polylineOptions = drawingManager.value.get('polylineOptions');
    polylineOptions.strokeColor = color;
    drawingManager.value.set('polylineOptions', polylineOptions);

    const rectangleOptions = drawingManager.value.get('rectangleOptions');
    rectangleOptions.fillColor = color;
    drawingManager.value.set('rectangleOptions', rectangleOptions);

    const circleOptions = drawingManager.value.get('circleOptions');
    circleOptions.fillColor = color;
    drawingManager.value.set('circleOptions', circleOptions);

    const polygonOptions = drawingManager.value.get('polygonOptions');
    polygonOptions.fillColor = color;
    drawingManager.value.set('polygonOptions', polygonOptions);
}

function setSelectedShapeColor(color: string) {
    if (!selectedShapeEmpty.value) {
        if (
            selectedShape.value.type == google.maps.drawing.OverlayType.POLYLINE
        ) {
            selectedShape.value.overlay.set('strokeColor', color);
        } else {
            selectedShape.value.overlay.set('fillColor', color);
        }
    }
}

async function initialize() {
    // Creates a drawing manager attached to the map that allows the user to draw
    // markers, lines, and shapes.
    drawingManager.value = new google.maps.drawing.DrawingManager({
        drawingMode: null,
        drawingControl: false,
        markerOptions: {
            draggable: true,
        },
        polylineOptions: lineOptions,
        rectangleOptions: shapeOptions,
        circleOptions: shapeOptions,
        polygonOptions: shapeOptions,
    });
    drawingManager.value.setMap(map.value);

    const isGeometry = (
        shape: google.maps.drawing.OverlayCompleteEvent
    ): shape is TypedShape =>
        ![null, google.maps.drawing.OverlayType.MARKER].includes(shape.type);

    drawingManager.value.addListener(
        'overlaycomplete',
        (shape: google.maps.drawing.OverlayCompleteEvent) => {
            if (isGeometry(shape)) {
                const typedShape = { ...shape, id: `${Date.now()}` };
                shapeList.value.push(typedShape);
                // Switch back to non-drawing mode after drawing a shape.

                // Add an event listener that selects the newly-drawn shape when the user
                // mouses down on it.
                shape.overlay.addListener('click', function () {
                    setSelection(typedShape.id);
                });
                // google.maps.event.addListener(newShape, "click", function () {
                //     setSelection(newShape);
                // });
                setSelection(typedShape.id);
            }
        }
    );

    // Clear the current selection when the drawing mode is changed,
    // or when the map is clicked.
    drawingManager.value.addListener('drawingmode_changed', clearSelection);
    map.value.addListener('click', clearSelection);
    selectColor(colors.value[0]);
    centerMap();
}
window.addEventListener('load', initialize);

function setMapType(type: google.maps.MapTypeId) {
    map.value?.setMapTypeId(type);
}
function setToolType(type: google.maps.drawing.OverlayType) {
    drawingManager.value.setDrawingMode(type);
}
function setShapeColor(color: string) {
    selectColor(color);
    setSelectedShapeColor(color);
}

const selectedShapeEmpty = computed(() => selectedShape.value === undefined);
const shapeListEmpty = computed(() => shapeList.value?.length === 0);
</script>

<template>
    <div class="flex flex-col gap-2">
        <div class="flex flex-row gap-2 items-end justify-between">
            <Button
                severity="secondary"
                :disabled="clientPosError !== null"
                icon="mdi mdi-crosshairs-gps"
                @click="() => centerMap()"
            >
                <template #icon>
                    <MdiIcon :icon="mdiCrosshairsGps" />
                </template>
            </Button>
            <MapTypeSelectButton @update:model-value="setMapType" />
        </div>
        <div class="h-[500px] overflow-hidden rounded-md">
            <GoogleMap
                ref="mapComponentRef"
                style="height: 500px; width: 100%"
                :api-key
                :libraries="['drawing']"
                :zoom="15"
                :center="mapCenter"
                :disable-default-ui="true"
            />
        </div>
        <div class="flex flex-row gap-2 items-center justify-stretch flex-wrap">
            <div class="flex flex-col gap-2 grow basis-50">
                <DrawingToolSelectButton @update:model-value="setToolType" />
                <ShapeColorSelectButton @update:model-value="setShapeColor" />
            </div>
            <div class="flex flex-col gap-2 grow basis-5 text-nowrap">
                <Button
                    severity="secondary"
                    label="Delete Selected Shape"
                    @click="deleteSelectedShape"
                    :disabled="selectedShapeEmpty"
                >
                    <template #icon>
                        <MdiIcon class="mr-1.5" :icon="mdiDelete" />
                    </template>
                </Button>
                <Button
                    severity="secondary"
                    label="Delete All Shapes"
                    @click="deleteAllShapes"
                    :disabled="shapeListEmpty"
                >
                    <template #icon>
                        <MdiIcon class="mr-2" :icon="mdiDeleteSweep" />
                    </template>
                </Button>
            </div>
        </div>
    </div>
</template>
