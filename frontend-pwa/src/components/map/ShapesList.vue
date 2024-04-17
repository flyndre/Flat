<script setup lang="ts">
import Button from 'primevue/button';
import IconField from 'primevue/iconfield';
import ScrollPanel from 'primevue/scrollpanel';
import InputIcon from '../icons/InputIcon.vue';
import InputText from 'primevue/inputtext';
import MdiIcon from '../icons/MdiIcon.vue';
import TextButtonIcon from '../icons/TextButtonIcon.vue';
import { IdentifyableTypedOverlay } from '@/types/map/IdentifyableTypedOverlay';
import {
    mdiChartLineVariant,
    mdiCircle,
    mdiCloseBox,
    mdiCrosshairs,
    mdiDeleteForever,
    mdiPentagon,
    mdiRectangle,
    mdiTextureBox,
} from '@mdi/js';
import { getShapeColor } from '@/util/googleMapsUtils';

type _Shape = IdentifyableTypedOverlay;

const props = withDefaults(
    defineProps<{
        shapes: _Shape[];
        centerShapeHook?: (id: _Shape) => any;
        setShapeNameHook?: (shape: _Shape, name: string) => any;
        deleteShapeHook?: (shape: _Shape) => any;
        deleteAllShapesHook?: () => any;
    }>(),
    {
        centerShapeHook: () => {},
        setShapeNameHook: () => {},
        deleteShapeHook: () => {},
        deleteAllShapesHook: () => {},
    }
);

function getShapeIcon(shape: IdentifyableTypedOverlay) {
    return shape.type === 'rectangle'
        ? mdiRectangle
        : shape.type === 'circle'
          ? mdiCircle
          : shape.type === 'polygon'
            ? mdiPentagon
            : mdiChartLineVariant;
}
</script>

<template>
    <ScrollPanel
        class="h-[40vh] max-h-[40vh]"
        :pt="{
            content: {
                class: 'flex flex-col gap-3.5 items-center justify-start',
            },
            barX: { class: 'hidden' },
        }"
    >
        <div
            class="w-full flex flex-row justify-between gap-2 overflow-auto shrink-0"
            v-for="shape of shapes"
        >
            <Button severity="secondary" @click="centerShapeHook(shape)">
                <template #icon>
                    <MdiIcon :icon="mdiCrosshairs" />
                </template>
            </Button>
            <IconField class="grow" icon-position="left">
                <InputIcon
                    :style="{
                        color: getShapeColor(shape),
                    }"
                    :icon="mdiTextureBox"
                />
                <InputText
                    class="w-full"
                    :model-value="shape.name"
                    placeholder="Area Name"
                    @update:model-value="
                        (v: string) => setShapeNameHook(shape, v)
                    "
                />
            </IconField>
            <Button
                class="shrink-0"
                severity="secondary"
                text
                @click="deleteShapeHook(shape)"
            >
                <template #icon>
                    <MdiIcon :icon="mdiDeleteForever" />
                </template>
            </Button>
        </div>
        <Button
            class="shrink-0"
            severity="danger"
            text
            :disabled="shapes.length === 0"
            @click="deleteAllShapesHook"
        >
            <template #default>
                <TextButtonIcon :icon="mdiCloseBox" />
                Delete All
            </template>
        </Button>
    </ScrollPanel>
</template>
