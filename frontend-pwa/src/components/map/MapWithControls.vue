<script setup lang="ts">
import {
    GOOGLE_MAPS_API_KEY,
    GOOGLE_MAPS_API_LIBRARIES,
} from '@/data/constants';
import { Division } from '@/types/Division';
import { IdentifyableTypedOverlay } from '@/types/map/IdentifyableTypedOverlay';
import { TypedOverlay } from '@/types/map/TypedOverlay';
import {
    geoJSONtoPolygon,
    getShapeBounds,
    getShapeColor,
    polygonToGeoJSON,
} from '@/util/googleMapsUtils';
import { isOnMobile } from '@/util/mobileDetection';
import { mdiDeleteForever, mdiMap, mdiPalette, mdiShape } from '@mdi/js';
import Button from 'primevue/button';
import Card from 'primevue/card';
import TabPanel from 'primevue/tabpanel';
import TabView from 'primevue/tabview';
import { v4 as uuidv4 } from 'uuid';
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { GoogleMap } from 'vue3-google-map';
import MdiIcon from '../icons/MdiIcon.vue';
import TextButtonIcon from '../icons/TextButtonIcon.vue';
import DrawingModeSwitch from './DrawingModeSwitch.vue';
import LocateMeButton from './LocateMeButton.vue';
import LocationSearchDialog from './LocationSearchDialog.vue';
import MapTypeSelectButton from './MapTypeSelectButton.vue';
import ShapeColorSelectButton from './ShapeColorSelectButton.vue';
import ShapesList from './ShapesList.vue';

/**
 * The shapes drawn on the map.
 */
const shapes = ref<IdentifyableTypedOverlay[]>([]);
const areas = defineModel<Division[]>('areas', {
    default: [],
});

const props = withDefaults(
    defineProps<{
        controls?: boolean;
    }>(),
    {
        controls: true,
    }
);

let recentlyUpdated = false;

const apiKey = GOOGLE_MAPS_API_KEY;
const libraries = GOOGLE_MAPS_API_LIBRARIES;
const mapComponentRef = ref<InstanceType<typeof GoogleMap> | null>();
const mapReady = computed(() => mapComponentRef.value?.ready);
const map = computed(() => mapComponentRef.value?.map);
const mapZoom = 15;

const placesService = ref<google.maps.places.PlacesService>();
const shapeSelected = ref(false);
const mapTypeId = ref<google.maps.MapTypeId>();

const stop = watch(mapReady, (v) => {
    if (!v) return;
    stop();
    syncAreas();
    if (areas.value.length > 0) {
        nextTick(() => panMapToShape(shapes.value[0]));
    }
    watch(() => areas.value, syncAreas, { deep: true });
});

function syncAreas() {
    if (recentlyUpdated) return; // Prevents infinite update loop
    deleteAllShapes(false);
    areas.value.forEach((a) => {
        const shape = {
            id: a.id,
            name: a.name,
            type: 'polygon',
            overlay: geoJSONtoPolygon(
                {
                    type: 'Polygon',
                    coordinates: a.area.coordinates[0],
                },
                {
                    ...shapeOptions,
                    strokeColor: a.color,
                    fillColor: a.color,
                }
            ),
        };
        shape.overlay.setMap(map.value);
        processNewOverlay(shape, false);
    });
    recentlyUpdated = true;
    shapes.value.length = 0;
    shapes.value.push(...all_overlays);
    clearSelection();
    nextTick(() => (recentlyUpdated = false));
}

const selectedToolRef = ref<google.maps.drawing.OverlayType>(null);
watch(selectedToolRef, (v) => drawingManager.value.setDrawingMode(v));

const selectedColorRef = ref<string>();
watch(selectedColorRef, (v) => setShapeColor(v));

function deleteShape(shape: IdentifyableTypedOverlay) {
    const index = all_overlays.findIndex((o) => o.id === shape.id);
    setSelection(all_overlays[index]?.overlay);
    deleteSelectedShape();
}
function centerShape(shape: IdentifyableTypedOverlay) {
    const index = all_overlays.findIndex((o) => o.id === shape.id);
    setSelection(all_overlays[index]?.overlay);
    panMapToShape(all_overlays[index]);
}
function setShapeName(shape: IdentifyableTypedOverlay, name: string) {
    const index = all_overlays.findIndex((o) => o.id === shape.id);
    all_overlays[index].name = name;
    shapeListChanged();
}

function panMapToPos(position: google.maps.LatLngLiteral | google.maps.LatLng) {
    try {
        map.value?.panTo(position);
    } catch (e) {}
    map.value?.setZoom(mapZoom);
}
function panMapToShape(shape: TypedOverlay) {
    map.value.fitBounds(getShapeBounds(shape));
}

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
    areas.value = shapes.value.map((s) => ({
        id: s.id,
        name: s.name,
        color: getShapeColor(s),
        area: {
            type: 'MultiPolygon',
            coordinates: [
                polygonToGeoJSON(<google.maps.Polygon>s.overlay).coordinates,
            ],
        },
    }));
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
    editable: false,
    draggable: false,
};
const lineOptions = {
    ...shapeOptions,
    strokeWeight: 4,
    strokeOpacity: 0.45,
};

/* 🟡 Custom */ function processNewOverlay(overlay: any, userCreated = true) {
    if (userCreated) {
        overlay.id = uuidv4();
        overlay.name = '';
    }
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
        class="flex gap-2 h-full justify-stretch items-stretch"
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
            v-if="controls"
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
                            <div
                                class="flex flex-row gap-2 items-center justify-stretch flex-nowrap grow"
                            >
                                <DrawingModeSwitch v-model="selectedToolRef" />
                                <Button
                                    severity="secondary"
                                    :disabled="!shapeSelected"
                                    @click="deleteSelectedShape"
                                >
                                    <template #icon>
                                        <MdiIcon :icon="mdiDeleteForever" />
                                    </template>
                                </Button>
                            </div>
                            <ShapeColorSelectButton
                                v-model="selectedColorRef"
                            />
                        </div>
                    </TabPanel>
                    <TabPanel>
                        <template #header>
                            <div class="flex justify-center items-center">
                                <TextButtonIcon :icon="mdiShape" />
                                Areas
                            </div>
                        </template>
                        <ShapesList
                            :shapes
                            :center-shape-hook="centerShape"
                            :delete-shape-hook="deleteShape"
                            :delete-all-shapes-hook="deleteAllShapes"
                            :set-shape-name-hook="setShapeName"
                        />
                    </TabPanel>
                </TabView>
            </template>
        </Card>
    </div>
</template>
