<script setup lang="ts">
import { mdiVectorSquareEdit, mdiVectorSquareRemove } from '@mdi/js';
import Button from 'primevue/button';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { computed } from 'vue';

const model = defineModel<google.maps.drawing.OverlayType>();
const isDrawing = computed(() => model.value !== null);
const messageCode = computed(
    () =>
        'components.draw_shape_button.' +
        (isDrawing.value ? 'stop_drawing' : 'draw_division')
);

function toggle() {
    model.value = <google.maps.drawing.OverlayType>(
        (isDrawing.value ? null : 'polygon')
    );
}
</script>

<template>
    <Button
        class="grow w-full whitespace-nowrap"
        severity="secondary"
        :text="isDrawing"
        :label="$t(messageCode)"
        @click="toggle"
    >
        <template #icon>
            <MdiTextButtonIcon
                :icon="isDrawing ? mdiVectorSquareRemove : mdiVectorSquareEdit"
            />
        </template>
    </Button>
</template>
