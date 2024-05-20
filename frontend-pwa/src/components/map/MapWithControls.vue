<script setup lang="ts">
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import DeleteShapeButton from '@/components/map/controls/DeleteShapeButton.vue';
import DrawShapeButton from '@/components/map/controls/DrawShapeButton.vue';
import LocateMeButton from '@/components/map/controls/LocateMeButton.vue';
import LocateShapesButton from '@/components/map/controls/LocateShapesButton.vue';
import LocationSearchDialog from '@/components/map/controls/LocationSearchDialog.vue';
import MapTypeSelectButton from '@/components/map/controls/MapTypeSelectButton.vue';
import ShapeColorSelectButton from '@/components/map/controls/ShapeColorSelectButton.vue';
import ShapesList from '@/components/map/controls/ShapesList.vue';
import { MarkerWithLabel } from '@googlemaps/markerwithlabel';
import {
    GOOGLE_MAPS_API_KEY,
    GOOGLE_MAPS_API_LIBRARIES,
} from '@/data/constants';
import {
    DARK_MAP_STYLES,
    LABELS_OFF_STYLES,
    markerHtmlFromTrack,
    POSITION_ICON_INNER,
    POSITION_ICON_OUTER,
} from '@/data/googleMapsPresets';
import { useTheme } from '@/plugins/ThemePlugin';
import { Division } from '@/types/Division';
import { IdentifyableTypedOverlay } from '@/types/map/IdentifyableTypedOverlay';
import { TypedOverlay } from '@/types/map/TypedOverlay';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import {
    divisionToShape,
    shapeToDivision,
    trackToShapeList,
} from '@/util/converters';
import {
    getShapeBounds,
    getShapeListBounds,
    shapeToGeoJSON,
} from '@/util/googleMapsUtils';
import { isOnMobile } from '@/util/mobileDetection';
import { mdiLock, mdiMap, mdiPalette, mdiTextureBox } from '@mdi/js';
import Card from 'primevue/card';
import TabPanel from 'primevue/tabpanel';
import TabView from 'primevue/tabview';
import { v4 as uuidv4 } from 'uuid';
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { GoogleMap } from 'vue3-google-map';
import MdiIcon from '../icons/MdiIcon.vue';
import { computedWithControl } from '@vueuse/core';
import { clientId } from '@/data/clientMetadata';

/**
 * The shapes drawn on the map.
 */
const shapes = ref<IdentifyableTypedOverlay[]>([]);
const divisions = defineModel<Division[]>('divisions', {
    default: [],
});

const props = withDefaults(
    defineProps<{
        controls?: 'none' | 'drawing' | 'minimal';
        labels?: boolean;
        mapType?: `${google.maps.MapTypeId}`;
        clientPos?: google.maps.LatLngLiteral;
        center?: google.maps.LatLngLiteral | 'area' | 'position';
        tracks?: ParticipantTrack[];
        locked?: boolean;
    }>(),
    {
        controls: 'minimal',
        labels: true,
        mapType: 'roadmap',
        locked: false,
    }
);

watch(() => props.clientPos, setPositionMarker);

const mapCenter = computedWithControl(
    () => [props.center, props.clientPos, shapes.value],
    () => {
        if (props.center === 'position') {
            panMapToPos(props.clientPos);
            return props.clientPos;
        }
        if (props.center === 'area') {
            if (all_overlays?.length > 0) panMapToShapes(all_overlays);
            return map.value?.getCenter();
        }
        return props.center ?? map.value?.getCenter();
    }
);

const sanitizedClientPos = computed(() => {
    if (
        props.clientPos == null ||
        props.clientPos.lat == null ||
        props.clientPos.lng == null
    ) {
        return undefined;
    }
    return props.clientPos;
});

let recentlyUpdated = false;

const apiKey = GOOGLE_MAPS_API_KEY;
const libraries = GOOGLE_MAPS_API_LIBRARIES;
const mapComponentRef = ref<InstanceType<typeof GoogleMap> | null>();
const mapReady = computed(() => mapComponentRef.value?.ready);
const map = computed(() => mapComponentRef.value?.map);
const mapZoom = 15;
const mapRestriction: google.maps.MapRestriction = {
    strictBounds: true,
    latLngBounds: {
        north: 85,
        south: -85,
        west: -180,
        east: 180,
    },
};

const placesService = ref<google.maps.places.PlacesService>();
const shapeSelected = ref(false);
const mapTypeId = ref<`${google.maps.MapTypeId}`>(props.mapType);
watch(
    () => props.mapType,
    (v) => (mapTypeId.value = v)
);

const { activeTheme } = useTheme();
const mapStyles = computed<google.maps.MapTypeStyle[]>(() => [
    ...(activeTheme.value === 'dark' ? DARK_MAP_STYLES : []),
    ...(props.labels ? [] : LABELS_OFF_STYLES),
]);

const stop = watch(mapReady, (v) => {
    if (!v) return;
    stop();
    syncAreas();
    watch(() => divisions.value, syncAreas, { deep: true });
    drawTracks();
    watch(() => props.tracks, drawTracks, { deep: true });
});

function syncAreas() {
    if (recentlyUpdated) return; // Prevents infinite update loop
    deleteAllShapes(false);
    divisions.value.forEach((d) => {
        if (d.area == null) return;
        const shape = divisionToShape(
            d,
            d.id === '0'
                ? {
                      ...shapeOptions,
                      strokeOpacity: 0.1,
                      fillOpacity: 0,
                  }
                : shapeOptions
        );
        shape.overlay.setMap(map.value);
        processNewOverlay(shape, false);
    });
    recentlyUpdated = true;
    shapes.value.length = 0;
    shapes.value.push(...all_overlays);
    clearSelection();
    if (props.center === 'area' && shapes.value?.length > 0)
        panMapToShapes(all_overlays);
    nextTick(() => (recentlyUpdated = false));
}

let progressLines: IdentifyableTypedOverlay[] = [];
let progressLabels: MarkerWithLabel[] = [];
function drawTracks() {
    progressLines.forEach((l) => l.overlay.setMap(null));
    progressLines.length = 0;
    progressLabels.forEach((l) => l.setMap(null));
    progressLabels.length = 0;
    props.tracks?.forEach((t) => {
        const shapes = trackToShapeList(t, {
            strokeColor: t.color,
            fillColor: t.color,
            editable: false,
            draggable: false,
        });
        console.log(t); 
        shapes?.forEach((s) => s.overlay?.setMap(map.value));
        progressLines.push(...shapes);
        const lastPosition = t.progress?.at(-1)?.track?.coordinates?.at(-1);
        if (lastPosition != null && t.id !== clientId.value) {
            const label = new MarkerWithLabel({
                position: {
                    lat: lastPosition[0],
                    lng: lastPosition[1],
                },
                labelContent: markerHtmlFromTrack(t),
                labelAnchor: new google.maps.Point(9, -6),
                clickable: false,
                draggable: false,
                cursor: 'default',
                icon: {
                    ...POSITION_ICON_INNER,
                    fillColor: t.color,
                    anchor: new google.maps.Point(12, 12),
                },
            });
            progressLabels.push(label);
            label.setMap(map.value);
        }
    });
}

const selectedToolRef = ref<google.maps.drawing.OverlayType>(null);
watch(selectedToolRef, (v) => drawingManager.setDrawingMode(v));

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

let marker_inner;
let marker_outer;
function setPositionMarker(
    position: google.maps.LatLngLiteral | google.maps.LatLng
) {
    if (mapReady.value) {
        if (marker_inner === undefined || marker_outer === undefined) {
            marker_outer = new google.maps.Marker({
                position,
                map: map.value,
                icon: {
                    ...POSITION_ICON_OUTER,
                    anchor: new google.maps.Point(12, 12),
                },
            });
            marker_inner = new google.maps.Marker({
                position,
                map: map.value,
                icon: {
                    ...POSITION_ICON_INNER,
                    anchor: new google.maps.Point(12, 12),
                },
            });
        }
        marker_inner.setPosition(position);
        marker_outer.setPosition(position);
    }
}

function panMapToPos(position: google.maps.LatLngLiteral | google.maps.LatLng) {
    if (position == null || Object.values(position).includes(null)) {
        return;
    }
    try {
        map.value?.panTo(position);
    } catch (e) {
        console.log(e);
    }
    map.value?.setZoom(mapZoom);
}
function panMapToShape(shape: TypedOverlay) {
    map.value.fitBounds(getShapeBounds(shape));
}
function panMapToShapes(shapes: TypedOverlay[]) {
    map.value.fitBounds(getShapeListBounds(shapes));
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
    divisions.value = shapes.value.map((s) => shapeToDivision(s));
    recentlyUpdated = true;
    nextTick(() => (recentlyUpdated = false));
}

/**
 * A setup for a Google Maps Map with shape tracking.
 * Please be careful when changing and prefix changes with `/* 🟡 Custom *\/;`.
 * @see https://stackoverflow.com/a/12006751/11793652
 */

/* 🟡 Custom */ var area_overlay;
var drawingManager: google.maps.drawing.DrawingManager;
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
    [
        'polylineOptions',
        'rectangleOptions',
        'circleOptions',
        'polygonOptions',
    ].forEach((optionsKey) => {
        const options = drawingManager.get(optionsKey);
        options.strokeColor = color;
        options.fillColor = color;
        drawingManager.set(optionsKey, options);
    });
}

function setSelectedShapeColor(color) {
    if (selectedShape) {
        selectedShape.set('strokeColor', color);
        selectedShape.set('fillColor', color);
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
    // strokeWeight: 0,
    // fillOpacity: 0.45,
    editable: true,
    draggable: true,
};
const lineOptions = {
    ...shapeOptions,
    strokeWeight: 4,
    strokeOpacity: 0.45,
};

/* 🟡 Custom */ function processNewOverlay(
    typedOverlay: any,
    userCreated = true
) {
    if (userCreated) {
        if (shapeToGeoJSON(typedOverlay).coordinates?.[0]?.length <= 2) {
            typedOverlay.overlay.setMap(null);
            selectedToolRef.value = null;
            return;
        }
        typedOverlay.id = uuidv4();
        typedOverlay.name = '';
    }
    all_overlays.push(typedOverlay);
    if (typedOverlay.type != google.maps.drawing.OverlayType.MARKER) {
        // Switch back to non-drawing mode after drawing a shape.
        // drawingManager.setDrawingMode(null);
        /* 🟡 Custom */ if (userCreated) selectedToolRef.value = null;

        // Add an event listener that selects the newly-drawn shape when the user
        // mouses down on it.
        var newShape = typedOverlay.overlay;
        newShape.type = typedOverlay.type;
        google.maps.event.addListener(newShape, 'click', function () {
            if (props.locked || props.controls !== 'drawing') return;
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

    /* 🟡 Custom */ placesService.value = new google.maps.places.PlacesService(
        map.value
    );
}
onMounted(initialize);
</script>

<template>
    <Card
        class="h-full basis-0 grow"
        :class="[
            {
                'shadow-none': controls === 'none',
            },
        ]"
        :pt="{
            root: { class: isOnMobile ? 'flex-col' : 'flex-col-reverse' },
            body: {
                class: [
                    controls !== 'none' ? 'p-2.5' : 'p-0',
                    { 'pb-0': controls === 'drawing' && isOnMobile },
                    { 'pt-0': controls === 'drawing' && !isOnMobile },
                ],
            },
            header: {
                class: 'h-full flex flex-col-reverse justify-stretch relative rounded-2xl overflow-hidden',
            },
        }"
    >
        <template #header>
            <GoogleMap
                version="beta"
                ref="mapComponentRef"
                background-color="transparent"
                style="height: 100%; width: 100%"
                :api-key
                :libraries
                :center="mapCenter"
                :zoom="mapZoom"
                :restriction="mapRestriction"
                :disable-default-ui="true"
                :map-type-id="mapTypeId"
                :styles="mapStyles"
                :clickable-icons="false"
                :draggable="!locked"
            />
            <MdiIcon
                v-if="mapReady && locked"
                class="absolute bottom-[0.65rem] left-[5.15rem] text-white [&:not(dark)]:opacity-60 dark:!opacity-100 stroke-black stroke-[0.8px] transition-opacity"
                :icon="mdiLock"
            />
        </template>
        <template #content v-if="controls === 'minimal'">
            <div class="flex flex-row gap-2">
                <LocateMeButton
                    :client-pos="sanitizedClientPos"
                    :locate-me-handler="
                        (r) => {
                            panMapToPos(r);
                            setPositionMarker(r);
                        }
                    "
                />
                <LocateShapesButton
                    :shapes-present="shapes?.length > 0"
                    :locate-shapes-handler="() => panMapToShapes(all_overlays)"
                />
            </div>
        </template>
        <template #content v-if="controls === 'drawing'">
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
                                :client-pos="sanitizedClientPos"
                                :locate-me-handler="
                                    (r) => {
                                        panMapToPos(r);
                                        setPositionMarker(r);
                                    }
                                "
                            />
                            <LocateShapesButton
                                :shapes-present="shapes?.length > 0"
                                :locate-shapes-handler="
                                    () => panMapToShapes(all_overlays)
                                "
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
                            <DrawShapeButton v-model="selectedToolRef" />
                            <DeleteShapeButton
                                :shape-selected
                                :delete-shape-handler="deleteSelectedShape"
                            />
                        </div>
                        <ShapeColorSelectButton
                            class="basis-52 min-w-[240px]"
                            v-model="selectedColorRef"
                        />
                    </div>
                </TabPanel>
                <TabPanel>
                    <template #header>
                        <div class="flex justify-center items-center">
                            <MdiTextButtonIcon :icon="mdiTextureBox" />
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
</template>
