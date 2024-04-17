<script setup lang="ts">
import { mdiFitToScreen } from '@mdi/js';
import Button from 'primevue/button';
import { onMounted } from 'vue';
import MdiIcon from '../icons/MdiIcon.vue';

const props = withDefaults(
    defineProps<{
        shapesPresent: boolean;
        /** Whether the `locateMeHandler` should be called on mounted. @default true */
        initialPan?: boolean;
        /** A function to handle when the button is clicked. */
        locateShapesHandler: () => any;
    }>(),
    {
        initialPan: true,
    }
);

function locateShapes() {
    props.locateShapesHandler();
}

onMounted(() => {
    if (props.shapesPresent) {
        locateShapes();
    }
});
</script>

<template>
    <Button
        severity="secondary"
        :disabled="!shapesPresent"
        @click="locateShapes"
    >
        <template #icon>
            <MdiIcon :icon="mdiFitToScreen" />
        </template>
    </Button>
</template>
