<script setup lang="ts">
import {
    GOOGLE_MAPS_API_KEY,
    GOOGLE_MAPS_API_LIBRARIES,
} from '@/data/constants';
import { TypedOverlay } from '@/types/map/TypedOverlay';
import { isOnMobile } from '@/util/mobileDetection';
import {
    mdiChartLineVariant,
    mdiCircle,
    mdiClose,
    mdiCloseBox,
    mdiMap,
    mdiPalette,
    mdiPentagon,
    mdiRectangle,
    mdiShape,
} from '@mdi/js';
import Button from 'primevue/button';
import Card from 'primevue/card';
import TabPanel from 'primevue/tabpanel';
import TabView from 'primevue/tabview';
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { GoogleMap } from 'vue3-google-map';
import MdiIcon from '../icons/MdiIcon.vue';
import TextButtonIcon from '../icons/TextButtonIcon.vue';
import DrawingToolSelectButton from './DrawingToolSelectButton.vue';
import LocateMeButton from './LocateMeButton.vue';
import LocationSearchDialog from './LocationSearchDialog.vue';
import MapTypeSelectButton from './MapTypeSelectButton.vue';
import ShapeColorSelectButton from './ShapeColorSelectButton.vue';
import ScrollPanel from 'primevue/scrollpanel';
import { getShapeBounds } from '@/util/googleMapsUtils';

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

const apiKey = GOOGLE_MAPS_API_KEY;
const libraries = GOOGLE_MAPS_API_LIBRARIES;
const mapComponentRef = ref<InstanceType<typeof GoogleMap> | null>();
const mapReady = computed(() => mapComponentRef.value?.ready);
const map = computed(() => mapComponentRef.value?.map);
const mapZoom = 15;

const placesService = ref<google.maps.places.PlacesService>();

const mapTypeId = ref<google.maps.MapTypeId>();
const selectedToolRef = ref<google.maps.drawing.OverlayType>(null);
watch(selectedToolRef, (v) => setToolType(v));
function setToolType(type: google.maps.drawing.OverlayType) {
    drawingManager.value.setDrawingMode(type);
}
function panMapToPos(position: google.maps.LatLngLiteral | google.maps.LatLng) {
    try {
        map.value?.panTo(position);
    } catch (e) {}
    map.value?.setZoom(mapZoom);
}
function panMapToShape(shape: TypedOverlay) {
    map.value.fitBounds(getShapeBounds(shape));
    map.value.setZoom(mapZoom);
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

const drawingManager = ref<google.maps.drawing.DrawingManager>();
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
    var polylineOptions = drawingManager.value.get('polylineOptions');
    polylineOptions.strokeColor = color;
    drawingManager.value.set('polylineOptions', polylineOptions);

    var rectangleOptions = drawingManager.value.get('rectangleOptions');
    rectangleOptions.fillColor = color;
    drawingManager.value.set('rectangleOptions', rectangleOptions);

    var circleOptions = drawingManager.value.get('circleOptions');
    circleOptions.fillColor = color;
    drawingManager.value.set('circleOptions', circleOptions);

    var polygonOptions = drawingManager.value.get('polygonOptions');
    polygonOptions.fillColor = color;
    drawingManager.value.set('polygonOptions', polygonOptions);
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
    if (!mapReady.value) {
        return setTimeout(initialize, 100);
    }
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
        map: map.value,
    });

    google.maps.event.addListener(
        drawingManager.value,
        'overlaycomplete',
        /* 🟡 Custom */ (e) => processNewOverlay(e)
    );

    // Clear the current selection when the drawing mode is changed, or when the
    // map is clicked.
    google.maps.event.addListener(
        drawingManager.value,
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

    /* 🟡 Custom */ placesService.value = new google.maps.places.PlacesService(
        map.value
    );
}
onMounted(initialize);
</script>

<template>
    <div
        class="flex gap-2 h-full justify-stretch items-stretch pb-2"
        :class="[isOnMobile ? 'flex-col' : 'flex-col-reverse']"
    >
        <div class="grow overflow-hidden flex flex-col-reverse rounded-xl">
            <GoogleMap
                ref="mapComponentRef"
                style="height: 100%; width: 100%"
                :api-key
                :libraries
                :zoom="mapZoom"
                :disable-default-ui="true"
                :map-type-id="mapTypeId"
                :clickable-icons="false"
            />
        </div>
        <Card
            :class="[{ isOnMobile: 'rounded-b-none' }]"
            :pt="{ body: { class: isOnMobile ? 'pb-0' : 'pt-0' } }"
        >
            <template #content>
                <TabView
                    :pt="{
                        root: {
                            class:
                                'flex ' +
                                (isOnMobile ? 'flex-col-reverse' : 'flex-col'),
                        },
                        nav: {
                            class: isOnMobile ? 'mt-2' : 'mb-2',
                        },
                        inkbar: { class: 'rounded-t h-1' },
                        panelContainer: { class: 'p-0' },
                    }"
                >
                    <TabPanel>
                        <template #header>
                            <div class="flex justify-center items-center">
                                <TextButtonIcon :icon="mdiMap" />
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
                                    :initial-pan="true"
                                    :locate-me-handler="(r) => panMapToPos(r)"
                                />
                                <LocationSearchDialog
                                    :places-service
                                    :select-result-callback="
                                        (r) => panMapToPos(r.geometry?.location)
                                    "
                                />
                            </div>
                            <MapTypeSelectButton
                                class="basis-1/12"
                                v-model="mapTypeId"
                            />
                        </div>
                    </TabPanel>
                    <TabPanel>
                        <template #header>
                            <div class="flex justify-center items-center">
                                <TextButtonIcon :icon="mdiPalette" />
                                Tools
                            </div>
                        </template>
                        <div
                            class="flex flex-row gap-2 items-center justify-stretch flex-wrap"
                        >
                            <DrawingToolSelectButton
                                v-model="selectedToolRef"
                            />
                            <ShapeColorSelectButton
                                v-model="selectedColorRef"
                            />
                        </div>
                    </TabPanel>
                    <TabPanel>
                        <template #header>
                            <div class="flex justify-center items-center">
                                <TextButtonIcon :icon="mdiShape" />
                                Shapes
                            </div>
                        </template>
                        <ScrollPanel
                            class="h-[40vh] max-h-[40vh]"
                            :pt="{
                                content: {
                                    class: 'flex flex-col gap-2 items-center justify-start',
                                },
                            }"
                        >
                            <div
                                class="w-full flex flex-row justify-between gap-1 overflow-auto shrink-0"
                                v-for="(shape, i) of shapes"
                            >
                                <Button
                                    class="w-full text-left capitalize"
                                    :label="shape.type"
                                    severity="contrast"
                                    text
                                    @click="
                                        () => {
                                            setSelection(
                                                all_overlays[i]?.overlay
                                            );
                                            panMapToShape(shape);
                                        }
                                    "
                                >
                                    <template #icon>
                                        <TextButtonIcon
                                            :style="{
                                                color:
                                                    shape.overlay.get(
                                                        'fillColor'
                                                    ) ||
                                                    shape.overlay.get(
                                                        'strokeColor'
                                                    ),
                                            }"
                                            :icon="
                                                shape.type === 'rectangle'
                                                    ? mdiRectangle
                                                    : shape.type === 'circle'
                                                      ? mdiCircle
                                                      : shape.type === 'polygon'
                                                        ? mdiPentagon
                                                        : mdiChartLineVariant
                                            "
                                        />
                                    </template>
                                </Button>
                                <Button
                                    class="shrink-0"
                                    severity="secondary"
                                    rounded
                                    text
                                    @click="
                                        () => {
                                            setSelection(
                                                all_overlays[i]?.overlay
                                            );
                                            deleteSelectedShape();
                                        }
                                    "
                                >
                                    <template #icon>
                                        <MdiIcon :icon="mdiClose" />
                                    </template>
                                </Button>
                            </div>
                            <Button
                                class="shrink-0"
                                severity="danger"
                                text
                                :disabled="shapes.length === 0"
                                @click="() => deleteAllShapes()"
                            >
                                <template #default>
                                    <TextButtonIcon :icon="mdiCloseBox" />
                                    Delete All
                                </template>
                            </Button>
                        </ScrollPanel>
                    </TabPanel>
                </TabView>
            </template>
        </Card>
    </div>
</template>
