<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { Division } from '@/types/Division';
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
import Button from 'primevue/button';
import IconField from 'primevue/iconfield';
import InputText from 'primevue/inputtext';
import ScrollPanel from 'primevue/scrollpanel';

const divisions = defineModel<Division[]>('divisions', {
    required: true,
})

const emit = defineEmits<{
    centerDivision: [Division],
    deleteDivision: [Division, number],
    clearDivisions: []
}>();

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
            v-for="(d, i) of divisions"
            :key="d.id"
            class="w-full flex flex-row justify-between gap-2 overflow-auto shrink-0"
        >
            <Button severity="secondary" @click="emit('centerDivision', d)">
                <template #icon>
                    <MdiIcon :icon="mdiCrosshairs" />
                </template>
            </Button>
            <IconField class="grow" icon-position="left">
                <MdiInputIcon
                    :style="{
                        color: d.color,
                    }"
                    :icon="mdiTextureBox"
                />
                <InputText
                    class="w-full"
                    v-model="d.name"
                    :placeholder="$t('components.divisions_list.division_name')"
                />
            </IconField>
            <Button
                class="shrink-0"
                severity="secondary"
                text
                @click="emit('deleteDivision', d, i)"
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
            :disabled="divisions.length === 0"
            @click="emit('clearDivisions')"
        >
            <template #default>
                <MdiTextButtonIcon :icon="mdiCloseBox" />
                {{ $t('components.divisions_list.delete_all') }}
            </template>
        </Button>
    </ScrollPanel>
</template>
