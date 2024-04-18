<script setup lang="ts">
import { isOnMobile } from '@/util/mobileDetection';
import { mdiArrowLeft, mdiCheck, mdiCloseBox, mdiCrosshairs, mdiCrosshairsGps, mdiDeleteForever, mdiDotsHorizontal, mdiFitToScreen, mdiMagnify, mdiMap, mdiPalette, mdiTableColumn, mdiTableColumnRemove, mdiTextureBox, mdiVectorSquareEdit } from '@mdi/js';
import Button from 'primevue/button';
import ScrollPanel from 'primevue/scrollpanel';
import Sidebar from 'primevue/sidebar';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';

const visible = defineModel<boolean>('visible', {
    default: false,
});
</script>

<template>
    <Sidebar
        class="w-full max-w-[787px] h-fit rounded-t-xl"
        v-model:visible="visible"
        modal
        position="bottom"
        header="help"
        :block-scroll="true"
        :show-close-icon="false"
        :pt="{
            header: {
                class: 'flex flex-row justify-stretch gap-2',
            },
            content: {
                class: 'h-full flex flex-col justify-stretch items-stretch',
            },
        }"
    >
        <template #header>
            <div class="grow basis-0">
                <Button
                    v-if="!isOnMobile"
                    class="shrink-0"
                    label="Back"
                    severity="secondary"
                    @click="visible = false"
                    text
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </div>
            <span class="text-center font-bold">Help</span>
            <div class="grow basis-0"></div>
        </template>
        <template #default>
            <ScrollPanel class="h-[65vh]">
                <p>
                    This is the map edit screen. Here, you draw shapes on the
                    map, representing different divisions of the area in which
                    you are organizing your collection.
                </p>
                <p>
                    Later, when your collection is in progress, each participant
                    using Flat is assigned one or more of those divisions.
                </p>
                <p>
                    The following will describe how to use the different tab
                    menus and buttons:
                </p>
                <p>
                    <p class="font-bold border border-solid rounded-lg p-2 w-max mt-8">
                        <MdiIcon
                            class="overflow-visible ml-1 mr-1.5"
                            :icon="mdiCheck"
                        />
                        Save
                    </p>
                    The "Save" button will save the areas you created to your collection.
                </p>
                <p>
                    <p class="font-bold border border-solid rounded-lg p-2 w-max mt-8">
                        <MdiIcon
                            class="overflow-visible ml-1 mr-1.5"
                            :icon="mdiMap"
                        />
                        Map
                    </p>
                    The Map tab provides you with everything you need to navigate the map:
                    <ul class="list-none [&>*]:mb-2.5 [&>*]:relative [&>li>*]:absolute [&>li>*]:top-1 [&>li>*]:-left-8">
                        <li>
                            <MdiIcon
                                :icon="mdiCrosshairsGps"
                            />
                            "Locate Me" button to pan to your current location
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiFitToScreen"
                            />
                            "Locate Shapes" button to pan the map so that all drawn areas are visible
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiMagnify"
                            />
                            A search to find a specific location
                        </li>
                        
                        <li>
                            <MdiIcon
                                class="rotate-90"
                                :icon="mdiTableColumn"
                            />
                            A select button to set the map type (roadmap, terrain, satellite and satellite with labels)
                        </li>
                    </ul>
                </p>

                <p>
                    <p class="font-bold border border-solid rounded-lg p-2 w-max mt-8">
                        <MdiIcon
                            class="overflow-visible ml-1 mr-1.5"
                            :icon="mdiPalette"
                        />
                        Tools
                    </p>
                    The Tools tab contains anything you need to draw on the map, such as:
                    <ul class="list-none [&>*]:mb-2.5 [&>*]:relative [&>li>*]:absolute [&>li>*]:top-1 [&>li>*]:-left-8">
                        <li>
                            <MdiIcon
                                :icon="mdiVectorSquareEdit"
                            />
                            "Draw Area" button to draw a new area onto the map and (while drawing) to stop drawing
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiDeleteForever"
                            />
                            A button to delete the currently selected area
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiDotsHorizontal"
                            />
                            Several colors to choose from for the areas
                        </li>
                    </ul>
                </p>

                <p>
                    <p class="font-bold border border-solid rounded-lg p-2 w-max mt-8">
                        <MdiIcon
                            class="overflow-visible ml-1 mr-1.5"
                            :icon="mdiTextureBox"
                        />
                        Areas
                    </p>
                    Here, you'll find an overview of all maps that have been drawn onto the map.
                    For each shape, you can:
                    <ul class="list-none [&>*]:mb-2.5 [&>*]:relative [&>li>*]:absolute [&>li>*]:top-1 [&>li>*]:-left-8">
                        <li>
                            <MdiIcon
                                :icon="mdiCrosshairs"
                            />
                            Focus the area on the map
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiTextureBox"
                            />
                            Give the area a name
                        </li>
                        <li>
                            <MdiIcon
                                :icon="mdiDeleteForever"
                            />
                            Delete the area
                        </li>
                    </ul>
                    At the bottom of the list you'll find a button to delete all areas:
                    <ul class="list-none [&>*]:mb-2.5 [&>*]:relative [&>li>*]:absolute [&>li>*]:top-1 [&>li>*]:-left-8">
                        <li>
                            <MdiIcon
                                :icon="mdiCloseBox"
                            />
                            Delete all
                        </li>
                    </ul>
                </p>
            </ScrollPanel>
            <Button
                v-if="isOnMobile"
                class="w-full sticky bottom-0 left-0 right-0 z-10 shrink-0 mt-5"
                label="Close"
                severity="secondary"
                @click="visible = false"
                text
            />
        </template>
    </Sidebar>
</template>
