<script setup lang="ts">
import { mdiVectorSquareEdit, mdiVectorSquareRemove } from '@mdi/js';
import Button from 'primevue/button';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { computed } from 'vue';

const model = defineModel<google.maps.drawing.OverlayType>();
const isDrawing = computed(() => model.value !== null);

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
        :label="isDrawing ? 'Stop Drawing' : 'Draw Area'"
        @click="toggle"
    >
        <template #icon>
            <MdiTextButtonIcon
                :icon="isDrawing ? mdiVectorSquareRemove : mdiVectorSquareEdit"
            />
        </template>
    </Button>
</template>
