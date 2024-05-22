<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { IdentifyableTypedOverlay } from '@/types/map/IdentifyableTypedOverlay';
import { getShapeColor } from '@/util/googleMapsUtils';
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
import Button from 'primevue/button';
import IconField from 'primevue/iconfield';
import InputText from 'primevue/inputtext';
import ScrollPanel from 'primevue/scrollpanel';

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
        class="h-[50vh] max-h-[50vh]"
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
                <MdiInputIcon
                    :style="{
                        color: getShapeColor(shape),
                    }"
                    :icon="mdiTextureBox"
                />
                <InputText
                    class="w-full"
                    :model-value="shape.name"
                    :placeholder="$t('components.divisions_list.division_name')"
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
                <MdiTextButtonIcon :icon="mdiCloseBox" />
                {{ $t('components.divisions_list.delete_all') }}
            </template>
        </Button>
    </ScrollPanel>
</template>
