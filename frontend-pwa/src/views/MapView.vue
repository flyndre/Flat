<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { GoogleMap } from "vue3-google-map";
import { useGeolocation } from "@vueuse/core";
import Button from "primevue/button";
import Card from "primevue/card";
import MdiIcon from "@/components/MdiIcon.vue";
import SelectButton from "primevue/selectbutton";
import InputGroup from "primevue/inputgroup";
import {
    mdiDeleteSweep,
    mdiDelete,
    mdiCrosshairsGps,
    mdiRoadVariant,
    mdiTerrain,
    mdiEarth,
    mdiEarthPlus,
    mdiHandBackLeft,
    mdiHandBackRightOutline,
    mdiRectangleOutline,
    mdiCircleOutline,
    mdiPentagonOutline,
    mdiChartLineVariant,
    mdiVectorPolyline,
    mdiVectorTriangle,
    mdiCircle,
} from "@mdi/js";

const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
const mapElement = ref();
const map = computed<undefined | google.maps.Map>(() => mapElement.value?.map);
const { coords: clientPos, error: clientPosError } = useGeolocation();
const coordsUnavailable = computed(
    () =>
        clientPos.value.latitude === null || clientPos.value.longitude === null
);
// watch(
//     coords,
//     (v) =>
//         (mapCenter.value = {
//             lat: v.latitude,
//             lng: v.longitude,
//         })
// );
const mapCenter = ref({
    lat: 0,
    lng: 0,
});

function centerMap() {
    try {
        map.value.panTo({
            lat: clientPos.value.latitude,
            lng: clientPos.value.longitude,
        });
    } catch (e) {}
    map.value.setZoom(14);
}

function exportBounds() {
    console.log(map.value.data);
}

function loadGeoJson() {
    map.value.data.loadGeoJson(
        "https://storage.googleapis.com/mapsdevsite/json/google.json"
    );
}

const mapTypes = [
    {
        value: "roadmap",
        icon: mdiRoadVariant,
    },
    {
        value: "terrain",
        icon: mdiTerrain,
    },
    {
        value: "satellite",
        icon: mdiEarth,
    },
    {
        value: "hybrid",
        icon: mdiEarthPlus,
    },
];
const selectedMapType = ref(mapTypes[0].value);
watch(selectedMapType, (t) => setMapType(t));

function setMapType(type: string) {
    map.value.setMapTypeId(type);
}

const toolTypes = [
    {
        value: null,
        icon: mdiHandBackRightOutline,
    },
    {
        value: "rectangle",
        icon: mdiRectangleOutline,
    },
    {
        value: "circle",
        icon: mdiCircleOutline,
    },
    {
        value: "polygon",
        icon: mdiVectorTriangle,
    },
    {
        value: "polyline",
        icon: mdiVectorPolyline,
    },
];
const selectedToolType = ref<any>(null);
watch(selectedToolType, (t) => setToolType(t));

function setToolType(type: any) {
    drawingManager.value.setDrawingMode(type);
}

/**
 * A setup for a Google Maps Map with shape tracking.
 * @see https://stackoverflow.com/a/12006751/11793652
 */

type Shape =
    | google.maps.Polygon
    | google.maps.Polyline
    | google.maps.Rectangle
    | google.maps.Circle;
type TypedShape = Shape & { type: google.maps.drawing.OverlayType };

// const selectedMapType = ref<>()
const drawingManager = ref<google.maps.drawing.DrawingManager>();
const shapeList = ref<google.maps.drawing.OverlayCompleteEvent[]>([]);
const selectedShape = ref<TypedShape>();
const colors = ref(["#1E90FF", "#FF1493", "#32CD32", "#FF8C00", "#4B0082"]);
const selectedColor = ref<string>();
const polyOptions = {
    strokeWeight: 0,
    fillOpacity: 0.45,
    editable: true,
    draggable: true,
};

function clearSelection() {
    if (selectedShape.value) {
        selectedShape.value.setEditable(false);
        selectedShape.value = null;
    }
}

function setSelection(shape) {
    clearSelection();
    selectedShape.value = shape;
    shape.setEditable(true);
    selectColor(shape.get("fillColor") || shape.get("strokeColor"));
}

function deleteSelectedShape() {
    if (selectedShape.value) {
        selectedShape.value.setMap(null);
        selectedShape.value = null;
    }
}

function deleteAllShapes() {
    selectedShape.value = null;
    shapeList.value.forEach((o) => o.overlay.setMap(null));
    shapeList.value = [];
}

function selectColor(color: string) {
    selectedColor.value = color;

    // Retrieves the current options from the drawing manager and replaces the
    // stroke or fill color as appropriate.
    const polylineOptions = drawingManager.value.get("polylineOptions");
    polylineOptions.strokeColor = color;
    drawingManager.value.set("polylineOptions", polylineOptions);

    const rectangleOptions = drawingManager.value.get("rectangleOptions");
    rectangleOptions.fillColor = color;
    drawingManager.value.set("rectangleOptions", rectangleOptions);

    const circleOptions = drawingManager.value.get("circleOptions");
    circleOptions.fillColor = color;
    drawingManager.value.set("circleOptions", circleOptions);

    const polygonOptions = drawingManager.value.get("polygonOptions");
    polygonOptions.fillColor = color;
    drawingManager.value.set("polygonOptions", polygonOptions);
}

function setSelectedShapeColor(color: string) {
    if (selectedShape.value) {
        if (
            selectedShape.value.type == google.maps.drawing.OverlayType.POLYLINE
        ) {
            selectedShape.value.set("strokeColor", color);
        } else {
            selectedShape.value.set("fillColor", color);
        }
    }
}

function clickColorButton(color: string) {
    selectColor(color);
    setSelectedShapeColor(color);
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
        polylineOptions: {
            editable: true,
        },
        rectangleOptions: polyOptions,
        circleOptions: polyOptions,
        polygonOptions: polyOptions,
        map: map.value,
    });

    drawingManager.value.addListener("overlaycomplete", function (e) {
        shapeList.value.push(e);
        console.log(e);
        if (e.type != google.maps.drawing.OverlayType.MARKER) {
            // Switch back to non-drawing mode after drawing a shape.
            drawingManager.value.setDrawingMode(null);

            // Add an event listener that selects the newly-drawn shape when the user
            // mouses down on it.
            const newShape = e.overlay;
            newShape.type = e.type;
            newShape.addListener("click", function () {
                setSelection(newShape);
            });
            google.maps.event.addListener(newShape, "click", function () {
                setSelection(newShape);
            });
            setSelection(newShape);
        }
    });

    // Clear the current selection when the drawing mode is changed, or when the
    // map is clicked.
    google.maps.event.addListener(
        drawingManager,
        "drawingmode_changed",
        clearSelection
    );
    map.value.addListener("click", clearSelection);
    drawingManager.value.addListener("drawingmode_changed", clearSelection);
    selectColor(colors.value[0]);
    centerMap();
}
window.addEventListener("load", initialize);
</script>

<template>
    <Card>
        <template #content>
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
                    <SelectButton
                        v-model="selectedMapType"
                        :options="mapTypes"
                        data-key="value"
                        option-value="value"
                        :allow-empty="false"
                        :pt="{ button: { class: 'h-9' } }"
                    >
                        <template #option="slotProps">
                            <MdiIcon :icon="slotProps.option.icon" />
                        </template>
                    </SelectButton>
                </div>
                <GoogleMap
                    class="rounded-lg h-[60vh] overflow-hidden"
                    ref="mapElement"
                    :api-key="apiKey"
                    :libraries="[
                        'drawing',
                        'geometry',
                        'localContext',
                        'visualization',
                    ]"
                    v-model="mapCenter"
                    :disable-default-ui="true"
                    :zoom="15"
                />
                <div
                    class="flex flex-row gap-2 items-center justify-stretch flex-wrap"
                >
                    <div class="flex flex-col gap-2 grow basis-50">
                        <SelectButton
                            v-model="selectedToolType"
                            :options="toolTypes"
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
                        <SelectButton
                            v-model="selectedColor"
                            :options="colors"
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
                                <MdiIcon
                                    @click="clickColorButton(slotProps.option)"
                                    :icon="mdiCircle"
                                    :style="{ color: slotProps.option }"
                                />
                            </template>
                        </SelectButton>
                    </div>
                    <div class="flex flex-col gap-2 grow basis-5 text-nowrap">
                        <Button
                            severity="secondary"
                            label="Delete Selected Shape"
                            @click="deleteSelectedShape"
                            :disabled="!selectedShape"
                        >
                            <template #icon>
                                <MdiIcon class="mr-1.5" :icon="mdiDelete" />
                            </template>
                        </Button>
                        <Button
                            severity="secondary"
                            label="Delete All Shapes"
                            @click="deleteAllShapes"
                            :disabled="shapeList.length === 0"
                        >
                            <template #icon>
                                <MdiIcon class="mr-2" :icon="mdiDeleteSweep" />
                            </template>
                        </Button>
                    </div>
                </div>
            </div>
        </template>
    </Card>
</template>

<style>
.color-button {
    width: 14px;
    height: 14px;
    font-size: 0;
    margin: 2px;
    float: left;
    cursor: pointer;
}
</style>
