<script setup lang="ts">
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import DeleteShapeButton from '@/components/map/controls/DeleteShapeButton.vue';
import DrawShapeButton from '@/components/map/controls/DrawShapeButton.vue';
import LocateMeButton from '@/components/map/controls/LocateMeButton.vue';
import LocateShapesButton from '@/components/map/controls/LocateShapesButton.vue';
import LocationSearchDialog from '@/components/map/controls/LocationSearchDialog.vue';
import MapTypeSelectButton, { MapTypeOption } from '@/components/map/controls/MapTypeSelectButton.vue';
import ShapeColorSelectButton from '@/components/map/controls/ShapeColorSelectButton.vue';
import ShapesList from '@/components/map/controls/ShapesList.vue';
import { clientId } from '@/data/clientMetadata';
import { useTheme } from '@/plugins/ThemePlugin';
import { Division } from '@/types/Division';
import { MapType } from '@/types/map/MapType';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { asHexColor } from '@/util/colorUtils.js';
import { getDivisionBounds, getDivisionsBounds, toLngLatLike } from '@/util/geoUtils';
import { setStyleSafe } from '@/util/maplibreUtils.js';
import { getStyle } from '@/util/mapStyleUtils.js';
import { isOnMobile } from '@/util/mobileDetection';
import { mdiEarth, mdiLock, mdiMap, mdiPalette, mdiRoadVariant, mdiTerrain, mdiTextureBox } from '@mdi/js';
import { watchImmediate } from '@vueuse/core';
import { Position } from 'geojson';
import { LngLatBoundsLike, Map as Maplibre } from 'maplibre-gl';
import Card from 'primevue/card';
import TabPanel from 'primevue/tabpanel';
import TabView from 'primevue/tabview';
import { useToast } from 'primevue/usetoast';
import { TerraDraw, TerraDrawModeUndoRedo, TerraDrawPolygonMode, TerraDrawRenderMode, TerraDrawSelectMode, TerraDrawSessionUndoRedo, TerraDrawUndoRedoKeyboardShortcuts } from "terra-draw";
import { TerraDrawMapLibreGLAdapter } from "terra-draw-maplibre-gl-adapter";
import { onMounted, onUnmounted, ref, useTemplateRef, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import MdiIcon from '../icons/MdiIcon.vue';
import { TOAST_LIFE_LONG } from '@/data/constants.js';

const divisions = defineModel<Division[]>('divisions', {
    required: true,
});

const mapType = defineModel<MapType>('mapType', {
    default: 'roadmap'
})

const props = withDefaults(
    defineProps<{
        controls?: 'none' | 'drawing' | 'minimal';
        labels?: boolean;
        clientPos?: Position;
        center?: Position | 'area' | 'position';
        tracks?: ParticipantTrack[];
        locked?: boolean;
    }>(),
    {
        controls: 'minimal',
        labels: true,
        locked: false,
    }
);

const mapDefaults = {
    zoom: 15,
    pitch: 0,
    padding: 20,
    bearing: 0,
};

const colorOptions = [
    '#1E90FF',
    '#FF1493',
    '#32CD32',
    '#FF8C00',
    '#4B0082',
];

const mapTypeOptions: MapTypeOption[] = [
    {
        value: 'roadmap',
        icon: mdiRoadVariant,
    },
    {
        value: 'satellite',
        icon: mdiEarth,
    },
    {
        value: 'terrain',
        icon: mdiTerrain,
    },
];

const mapContainer = useTemplateRef('map-container');
const map = ref<Maplibre>()
const draw = ref<TerraDraw>()
const selectedDivision = ref<Division>()
const selectedMode = ref<'draw' | 'select' | 'render'>(props.locked ? 'render' : 'select');
const selectedColor = ref<string>(colorOptions[0]);
const { t } = useI18n()
const undoRedoMode = ref<TerraDrawModeUndoRedo>()
const undoRedoSession = ref<TerraDrawSessionUndoRedo>()
const { activeTheme } = useTheme();
const { add: addToast } = useToast();

watch(() => props.clientPos, (p) => p && setPositionMarker(p));

// const stop = watch(mapReady, (v) => {
//     if (!v) return;
//     stop();
//     // syncAreas();
//     // watch(() => divisions.value, syncAreas, { deep: true });
//     drawTracks();
//     watch(() => props.tracks, drawTracks, { deep: true });
// });

// function syncAreas() {
//     if (recentlyUpdated) return; // Prevents infinite update loop
//     deleteAllShapes(false);
//     divisions.value.forEach((d) => {
//         if (d.area == null) return;
//         const shape = divisionToShape(
//             d,
//             d.id === '0'
//                 ? {
//                     ...shapeOptions,
//                     strokeOpacity: 0.1,
//                     fillOpacity: 0,
//                 }
//                 : shapeOptions
//         );
//         // shape.overlay.setMap(map.value);
//         processNewOverlay(shape, false);
//     });
//     recentlyUpdated = true;
//     shapes.value.length = 0;
//     shapes.value.push(...all_overlays);
//     clearSelection();
//     if (props.center === 'area' && shapes.value?.length > 0)
//         focusDivisions(all_overlays);
//     nextTick(() => (recentlyUpdated = false));
// }

// let progressLines: IdentifyableTypedOverlay[] = [];
// let progressLabels: MarkerWithLabel[] = [];
function drawTracks() {
    // progressLines.forEach((l) => l.overlay.setMap(null));
    // progressLines.length = 0;
    // progressLabels.forEach((l) => l.setMap(null));
    // progressLabels.length = 0;
    // props.tracks?.forEach((t) => {
    //     const shapes = trackToShapeList(t, {
    //         strokeColor: t.color,
    //         fillColor: t.color,
    //         editable: false,
    //         draggable: false,
    //     });
    //     // shapes?.forEach((s) => s.overlay?.setMap(map.value));
    //     progressLines.push(...shapes);
    //     const lastPosition = t.progress?.at(-1)?.track?.coordinates?.at(-1);
    //     if (lastPosition != null && t.id !== clientId.value) {
    //         const label = new MarkerWithLabel({
    //             position: {
    //                 lng: lastPosition[0],
    //                 lat: lastPosition[1],
    //             },
    //             labelContent: markerHtmlFromTrack(t),
    //             labelAnchor: new google.maps.Point(6, -6),
    //             clickable: false,
    //             draggable: false,
    //             cursor: 'default',
    //             icon: {
    //                 ...POSITION_ICON_INNER,
    //                 fillColor: t.color,
    //                 scale: POSITION_ICON_INNER.scale * 0.6,
    //                 anchor: new google.maps.Point(12, 12),
    //             },
    //         });
    //         progressLabels.push(label);
    //         // label.setMap(map.value);
    //     }
    // });
}

let marker_inner;
let marker_outer;
function setPositionMarker(
    position: Position
) {
    // if (mapReady.value) {
    //     if (marker_inner === undefined || marker_outer === undefined) {
    //         marker_outer = new google.maps.Marker({
    //             position,
    //             map: map.value,
    //             icon: {
    //                 ...POSITION_ICON_OUTER,
    //                 anchor: new google.maps.Point(12, 12),
    //             },
    //         });
    //         marker_inner = new google.maps.Marker({
    //             position,
    //             map: map.value,
    //             icon: {
    //                 ...POSITION_ICON_INNER,
    //                 anchor: new google.maps.Point(12, 12),
    //             },
    //         });
    //     }
    //     marker_inner.setPosition(position);
    //     marker_outer.setPosition(position);
    // }
}

function panMapToPos(
    position: Position | undefined,
    animate = true,
    zoom: number | false = mapDefaults.zoom
) {
    if (position === undefined || position.length < 2) {
        return;
    }
    map.value?.setCenter(toLngLatLike(position), { animate, zoom });
}

function panMapToBounds(bounds: LngLatBoundsLike, animate = true) {
    const { padding, pitch, bearing, zoom: maxZoom } = mapDefaults;
    map.value?.fitBounds(bounds,  { padding, pitch, bearing, animate, maxZoom })
}

function focusDivision(division: Division, animate = true) {
    panMapToBounds(getDivisionBounds(division), animate);
}
function focusDivisions(divisions: Division[], animate = true) {
    if (divisions.length === 0) {
        return;
    }
    panMapToBounds(getDivisionsBounds(divisions), animate);
}

function deleteDivision(division: Division, index: number) {
    divisions.value.splice(index, 1);
    // TODO: Show toast with undo
}

function clearDivisions() {
    divisions.value.length = 0;
    // TODO: Show toast with undo
}

function deleteSelectedShape() {
    const id = selectedDivision.value?.id
    const index = divisions.value.findIndex(d => d.id === id);
    if (id && index >= 0) {
        divisions.value.splice(index, 1);
    }
}

function undo() {
    undoRedoMode.value?.undo() || undoRedoSession.value?.undo()
}

function redo() {
    undoRedoMode.value?.redo() || undoRedoSession.value?.redo()
}

onMounted(async () => {
    const { bearing, pitch, zoom } = mapDefaults;
    const m = new Maplibre({
        container: mapContainer.value!,
        interactive: true,
        zoom,
        pitch,
        bearing,
        attributionControl: false,
    });
    watchImmediate(() => [mapType.value, activeTheme.value] as const, ([type, theme]) => {
        setStyleSafe(m, getStyle(type, theme));
    });
    watchImmediate(
        () => [props.center, props.clientPos, divisions.value] as const,
        ([c, p, d]) => {
            if (c === 'position') {
                panMapToPos(p, false);
                return;
            }
            if (c === 'area') {
                focusDivisions(d, false);
                return;
            }
            if (c && c.length >= 2) {
                panMapToPos(c);
                return;
            }
        }
    );
    map.value = m
    await new Promise(r => m.on('style.load', r))
    undoRedoMode.value = new TerraDrawModeUndoRedo({ maxStackSize: 100 })
    undoRedoSession.value = new TerraDrawSessionUndoRedo({ maxStackSize: 100 })
    const d = new TerraDraw({
        adapter: new TerraDrawMapLibreGLAdapter({ map: m }),
        undoRedo: {
            modeLevel: undoRedoMode.value,
            sessionLevel: undoRedoSession.value,
            keyboardShortcuts: new TerraDrawUndoRedoKeyboardShortcuts(),
        },
        modes: props.locked
            ? [
                new TerraDrawRenderMode({
                    modeName: 'render',
                    styles: {
                        polygonFillColor: (f) => asHexColor(f.properties.color ?? selectedColor.value),
                        polygonOutlineColor: (f) => asHexColor(f.properties.color ?? selectedColor.value),
                    }
                })
            ]
            : [
                new TerraDrawPolygonMode({
                    modeName: 'draw',
                    editable: true,
                    styles: {
                        fillColor: (f) => asHexColor(f.properties.color ?? selectedColor.value),
                        outlineColor: (f) => asHexColor(f.properties.color ?? selectedColor.value),
                        closingPointColor: (f) => asHexColor(f.properties.color ?? selectedColor.value),
                        snappingPointColor: (f) => asHexColor(f.properties.color ?? selectedColor.value),
                    },
                }),
                new TerraDrawSelectMode({
                    modeName: 'select',
                    allowManualDeselection: true,
                    styles: {
                        selectedPolygonColor: (f) => asHexColor(f.properties.color),
                        selectedPolygonOutlineColor: (f) => asHexColor(f.properties.color),
                        midPointColor: () => asHexColor(selectedColor.value),
                        selectionPointColor: () => asHexColor(selectedColor.value), 
                    },
                    flags: {
                        'draw': {
                            feature: {
                                // The entire Feature can be moved
                                draggable: true,

                                // Individual coordinates that make up the Feature...
                                coordinates: {
                                    // Midpoint be added
                                    midpoints: {
                                        // Midpoint be dragged
                                        draggable: true
                                    },

                                    // Can be moved
                                    draggable: true,

                                    // Can snap to other coordinates from geometries _of the same mode_
                                    snappable: true,

                                    // Can be deleted
                                    deletable: true,
                                },
                            },
                        },
                    },
                }),
            ],
    });
    d.start();
    draw.value = d;
    d.on('finish', (id) => {
        const f = d.getSnapshotFeature(id)
        selectedMode.value = 'select'
        if (f && f.geometry.type === 'Polygon') {
            const div: Division = {
                id: id.toString(),
                area: f.geometry,
                clientId: clientId.value,
                color: selectedColor.value,
                name: undefined,
            }
            const index = divisions.value.findIndex(d => d.id === id.toString());
            if (index >= 0) {
                divisions.value[index] = div;
            } else {
                divisions.value.push(div);
            }
        }
    })
    d.on('select', (id) => {
        const f = d.getSnapshotFeature(id);
        if (f) {
            selectedDivision.value = divisions.value.find(d => d.id === id);
            const color = f.properties.color;
            if (color && typeof color === 'string') {
                selectedColor.value = color;
            }
        }
    });
    d.on('deselect', () => {
        selectedDivision.value = undefined;
    });
    watchImmediate(
        // Only use relevant properties as watch source.
        () => new Map(divisions.value.map(
            div => [div.id, ({
                id: div.id,
                geometry: div.area,
                properties: {
                    color: div.color ?? null,
                },
            })])
        ),
        (current, previous) => {
            const deletedIds = previous?.keys().filter(idA => !current.keys().some(idB => idA === idB)).toArray();
            if (deletedIds !== undefined) {
                d.removeFeatures(deletedIds);
            }

            for (const { id, geometry, properties } of current.values()) {
                if (d.hasFeature(id)) {
                    d.updateFeatureGeometry(id, geometry);
                    d.updateFeatureProperties(id, properties);
                } else {
                    d.addFeatures([{
                        id,
                        type: 'Feature',
                        geometry,
                        properties: {
                            mode: props.locked ? 'render' : 'draw',
                            ...properties,
                        },
                    }]);
                }
            }
        }, {
            deep: true,
        }
    );
    watchImmediate(() => selectedMode.value, (m) => {
        draw.value?.setMode(m)
    });
    watchImmediate(() => selectedColor.value, (c) => {
        if (selectedDivision.value) {
            selectedDivision.value.color = c;
        }
    });
    const { unsubscribe } = m.on('error', () => {
        addToast({
            severity: 'error',
            summary: t('components.map_with_controls.initialization_failed'),
            detail: t('components.map_with_controls.initialization_failed_text'),
            life: TOAST_LIFE_LONG,
        })
        unsubscribe(); // Only show error once.
    });
});

onUnmounted(() => {
    map.value?.remove();
});
</script>

<template>
    <Card class="h-full basis-0 grow" :class="[
        {
            'shadow-none': controls === 'none',
        },
    ]" :pt="{
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
    }">
        <template #header>
            <div ref="map-container" class="size-full" :class="{ 'pointer-events-none': locked }"></div>
            <MdiIcon v-if="locked"
                class="absolute left-4 bottom-4 text-white [&:not(dark)]:opacity-60 dark:!opacity-100 stroke-black stroke-[0.8px] transition-opacity"
                :icon="mdiLock" />
        </template>
        <template #content >
            <div v-if="controls === 'minimal'" class="flex flex-row gap-2">
                <LocateMeButton :client-pos @click="(p) => {
                    panMapToPos(p);
                    setPositionMarker(p);
                }" />
                <LocateShapesButton :shapes-present="divisions?.length > 0"
                    @click="focusDivisions(divisions)" />
            </div>
            <TabView  v-if="controls === 'drawing'" :pt="{
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
            }">
                <TabPanel>
                    <template #header>
                        <div class="flex justify-center items-center">
                            <MdiTextButtonIcon :icon="mdiMap" />
                            {{ t('components.map_with_controls.map') }}
                        </div>
                    </template>
                    <div class="flex flex-row gap-2 items-center justify-stretch flex-wrap">
                        <div class="flex flex-row gap-2 grow text-nowrap basis-7/12">
                            <LocateMeButton :client-pos @click="(p) => {
                                panMapToPos(p);
                                setPositionMarker(p);
                            }" />
                            <LocateShapesButton :shapes-present="divisions.length > 0" :initial-pan="true" @click="() => focusDivisions(divisions)"/>
                            <LocationSearchDialog @select="panMapToBounds" />
                        </div>
                        <MapTypeSelectButton class="basis-1/12" v-model="mapType" :options="mapTypeOptions" />
                    </div>
                </TabPanel>
                <TabPanel>
                    <template #header>
                        <div class="flex justify-center items-center">
                            <MdiTextButtonIcon :icon="mdiPalette" />
                            {{ t('components.map_with_controls.tools') }}
                        </div>
                    </template>
                    <div class="flex flex-row gap-2 items-center justify-stretch flex-wrap">
                        <div class="flex flex-row gap-2 items-center justify-stretch flex-nowrap max-md:grow">
                            <DrawShapeButton class="grow" v-model="selectedMode" />
                            <DeleteShapeButton class="shrink-0" :selected="!!selectedDivision" @click="deleteSelectedShape" @undo="undo" @redo="redo" />
                        </div>
                        <ShapeColorSelectButton class="grow shrink-0" v-model="selectedColor" :options="colorOptions" />
                    </div>
                </TabPanel>
                <TabPanel>
                    <template #header>
                        <div class="flex justify-center items-center">
                            <MdiTextButtonIcon :icon="mdiTextureBox" />
                            {{ t('components.map_with_controls.divisions') }}
                        </div>
                    </template>
                    <ShapesList
                        v-model:divisions="divisions"
                        @center-division="focusDivision"
                        @delete-division="deleteDivision"
                        @clear-divisions="clearDivisions"
                    />
                </TabPanel>
            </TabView>
        </template>
    </Card>
</template>
