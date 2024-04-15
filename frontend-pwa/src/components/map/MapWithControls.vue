<script setup lang="ts">
import { useGeolocation } from '@vueuse/core';
import { computed, ref, watch } from 'vue';
import { GoogleMap } from 'vue3-google-map';
import MdiIcon from '../icons/MdiIcon.vue';
import Button from 'primevue/button';
import { mdiCrosshairsGps, mdiDelete, mdiDeleteSweep } from '@mdi/js';
import DrawingToolSelectButton from './DrawingToolSelectButton.vue';
import ShapeColorSelectButton from './ShapeColorSelectButton.vue';
import MapTypeSelectButton from './MapTypeSelectButton.vue';
import { mapCenterWithDefaults } from '@/util/googleMapsUtils';
import { TypedOverlay } from '@/types/map/TypedOverlay';
import { nextTick } from 'vue';

/**
 * The shapes drawn on the map.
 */
const shapes = defineModel<TypedOverlay[]>('shapes', { default: [] });
let recentlyUpdated = false;

watch(
    // TODO: watch values not length as soon as shapes are inputted geojson
    () => shapes.value.length,
    () => {
        if (recentlyUpdated) return; // Prevents infinite update loop
        deleteAllShapes(false);
        shapes.value.forEach((s) => {
            s.overlay?.setMap(map.value);
            processNewOverlay(s, false);
        });
        recentlyUpdated = true;
        nextTick(() => (recentlyUpdated = false));
    }
);

const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
const mapComponentRef = ref<InstanceType<typeof GoogleMap> | null>();
const mapReady = computed(() => mapComponentRef.value?.ready);
const map = computed(() => mapComponentRef.value?.map);
const mapZoom = 15;
const { coords: clientPos, error: clientPosError } = useGeolocation();
const mapCenter = mapCenterWithDefaults(useGeolocation().coords, {
    lat: 0,
    lng: 0,
});

const mapTypeId = ref<google.maps.MapTypeId>();
const selectedToolRef = ref<google.maps.drawing.OverlayType>(null);
watch(selectedToolRef, (v) => setToolType(v));
function setToolType(type: google.maps.drawing.OverlayType) {
    drawingManager.setDrawingMode(type);
}
function centerMap() {
    try {
        map.value?.panTo({
            lat: clientPos.value.latitude,
            lng: clientPos.value.longitude,
        });
    } catch (e) {}
    map.value?.setZoom(mapZoom);
}

const selectedColorRef = ref<string>();
watch(selectedColorRef, (v) => setShapeColor(v));

const shapeSelected = ref(false);

function addShapeChangeListeners(shape: any) {
    switch (shape.type) {
        case google.maps.drawing.OverlayType.RECTANGLE:
            google.maps.event.addListener(
                shape,
                'bounds_changed',
                shapeListChanged
            );
            break;

        case google.maps.drawing.OverlayType.CIRCLE:
            google.maps.event.addListener(
                shape,
                'radius_changed',
                shapeListChanged
            );
            google.maps.event.addListener(
                shape,
                'center_changed',
                shapeListChanged
            );
            break;

        case google.maps.drawing.OverlayType.POLYGON:
            google.maps.event.addListener(
                shape.getPath(),
                'insert_at',
                shapeListChanged
            );
            google.maps.event.addListener(
                shape.getPath(),
                'remove_at',
                shapeListChanged
            );
            google.maps.event.addListener(
                shape.getPath(),
                'set_at',
                shapeListChanged
            );
            break;

        case google.maps.drawing.OverlayType.POLYLINE:
            google.maps.event.addListener(
                shape.getPath(),
                'insert_at',
                shapeListChanged
            );
            google.maps.event.addListener(
                shape.getPath(),
                'remove_at',
                shapeListChanged
            );
            google.maps.event.addListener(
                shape.getPath(),
                'set_at',
                shapeListChanged
            );
            break;

        case google.maps.drawing.OverlayType.MARKER:
            break;

        default:
            console.warn('Unrecognized overlay created:', shape);
            break;
    }
}
function shapeListChanged() {
    shapes.value.length = 0;
    shapes.value.push(...all_overlays);
    recentlyUpdated = true;
    nextTick(() => (recentlyUpdated = false));
}

/**
 * A setup for a Google Maps Map with shape tracking.
 * Please be careful when changing and prefix changes with `/* 🟡 Custom *\/;`.
 * @see https://stackoverflow.com/a/12006751/11793652
 */

var drawingManager;
var all_overlays = [];
var selectedShape;
var colors = ['#1E90FF', '#FF1493', '#32CD32', '#FF8C00', '#4B0082'];
var selectedColor;

function clearSelection() {
    if (selectedShape) {
        selectedShape.setEditable(false);
        selectedShape.setDraggable(false);
        selectedShape = null;
        /* 🟡 Custom */ shapeSelected.value = false;
    }
}

function setSelection(shape) {
    clearSelection();
    selectedShape = shape;
    shape.setEditable(true);
    shape.setDraggable(true);
    /* 🟡 Custom */ selectedColorRef.value =
        shape.get('fillColor') || shape.get('strokeColor');
    // selectColor(shape.get('fillColor') || shape.get('strokeColor'));
    /* 🟡 Custom */ shapeSelected.value = true;
}

function deleteSelectedShape() {
    if (selectedShape) {
        selectedShape.setMap(null);
        /* 🟡 Custom */ all_overlays = all_overlays.filter(
            (o) => o.overlay?.getMap() !== null
        );
        /* 🟡 Custom */ clearSelection();
        /* 🟡 Custom */ shapeListChanged();
    }
}

function deleteAllShapes(/* 🟡 Custom */ notify = true) {
    all_overlays.forEach((o) => o?.overlay?.setMap(null));
    // for (var i = 0; i < all_overlays.length; i++) {
    //     all_overlays[i].overlay.setMap(null);
    // }
    all_overlays = [];
    /* 🟡 Custom */ clearSelection();
    /* 🟡 Custom */ if (notify) shapeListChanged();
}

function selectColor(color) {
    selectedColor = color;

    // Retrieves the current options from the drawing manager and replaces the
    // stroke or fill color as appropriate.
    var polylineOptions = drawingManager.get('polylineOptions');
    polylineOptions.strokeColor = color;
    drawingManager.set('polylineOptions', polylineOptions);

    var rectangleOptions = drawingManager.get('rectangleOptions');
    rectangleOptions.fillColor = color;
    drawingManager.set('rectangleOptions', rectangleOptions);

    var circleOptions = drawingManager.get('circleOptions');
    circleOptions.fillColor = color;
    drawingManager.set('circleOptions', circleOptions);

    var polygonOptions = drawingManager.get('polygonOptions');
    polygonOptions.fillColor = color;
    drawingManager.set('polygonOptions', polygonOptions);
}

function setSelectedShapeColor(color) {
    if (selectedShape) {
        if (selectedShape.type == google.maps.drawing.OverlayType.POLYLINE) {
            selectedShape.set('strokeColor', color);
        } else {
            selectedShape.set('fillColor', color);
        }
        /* 🟡 Custom */ shapeListChanged();
    }
}

function setShapeColor(color: string) {
    selectColor(color);
    setSelectedShapeColor(color);
}

function buildColorPalette() {
    selectColor(colors[0]);
}

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

/* 🟡 Custom */ function processNewOverlay(overlay: any, userCreated = true) {
    all_overlays.push(overlay);
    if (overlay.type != google.maps.drawing.OverlayType.MARKER) {
        // Switch back to non-drawing mode after drawing a shape.
        // drawingManager.setDrawingMode(null);
        /* 🟡 Custom */ if (userCreated) selectedToolRef.value = null;

        // Add an event listener that selects the newly-drawn shape when the user
        // mouses down on it.
        var newShape = overlay.overlay;
        newShape.type = overlay.type;
        google.maps.event.addListener(newShape, 'click', function () {
            setSelection(newShape);
        });
        if (userCreated) {
            /* 🟡 Custom */ // Has to be next tick, otherwise the tool change above will unselect the selected shape
            /* 🟡 Custom */ nextTick(() => setSelection(newShape));
            /* 🟡 Custom */ shapeListChanged();
        }
        /* 🟡 Custom */ addShapeChangeListeners(newShape);
    }
}

async function initialize() {
    // Creates a drawing manager attached to the map that allows the user to draw
    // markers, lines, and shapes.
    drawingManager = new google.maps.drawing.DrawingManager({
        drawingMode: null,
        drawingControl: false,
        markerOptions: {
            draggable: true,
        },
        polylineOptions: lineOptions,
        rectangleOptions: shapeOptions,
        circleOptions: shapeOptions,
        polygonOptions: shapeOptions,
        map: map.value,
    });

    google.maps.event.addListener(
        drawingManager,
        'overlaycomplete',
        /* 🟡 Custom */ (e) => processNewOverlay(e)
    );

    // Clear the current selection when the drawing mode is changed, or when the
    // map is clicked.
    google.maps.event.addListener(
        drawingManager,
        'drawingmode_changed',
        clearSelection
    );
    google.maps.event.addListener(map.value, 'click', clearSelection);
    // google.maps.event.addDomListener(
    //     document.getElementById('delete-button'),
    //     'click',
    //     deleteSelectedShape
    // );
    // google.maps.event.addDomListener(
    //     document.getElementById('delete-all-button'),
    //     'click',
    //     deleteAllShape
    // );

    buildColorPalette();
}
window.addEventListener('load', initialize);
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
            <MapTypeSelectButton v-model="mapTypeId" />
        </div>
        <div class="h-[500px] overflow-hidden rounded-md">
            <GoogleMap
                ref="mapComponentRef"
                style="height: 500px; width: 100%"
                :api-key
                :libraries="['drawing']"
                :zoom="mapZoom"
                :center="mapCenter"
                :disable-default-ui="true"
                :map-type-id="mapTypeId"
            />
        </div>
        <div class="flex flex-row gap-2 items-center justify-stretch flex-wrap">
            <div class="flex flex-col gap-2 grow basis-50">
                <DrawingToolSelectButton v-model="selectedToolRef" />
                <ShapeColorSelectButton v-model="selectedColorRef" />
            </div>
            <div class="flex flex-col gap-2 grow basis-5 text-nowrap">
                <Button
                    severity="secondary"
                    label="Delete Selected Shape"
                    @click="deleteSelectedShape"
                    :disabled="!shapeSelected"
                >
                    <template #icon>
                        <MdiIcon class="mr-1.5" :icon="mdiDelete" />
                    </template>
                </Button>
                <Button
                    severity="secondary"
                    label="Delete All Shapes"
                    @click="() => deleteAllShapes()"
                >
                    <template #icon>
                        <MdiIcon class="mr-2" :icon="mdiDeleteSweep" />
                    </template>
                </Button>
            </div>
        </div>
    </div>
</template>
