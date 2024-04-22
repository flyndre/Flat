<script setup lang="ts">
import { mdiCrosshairsGps } from '@mdi/js';
import { onMounted } from 'vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import Button from 'primevue/button';

const props = withDefaults(
    defineProps<{
        /** Whether the `locateMeHandler` should be called on mounted. @default true */
        initialPan?: boolean;
        panOnUpdate?: boolean;
        clientPos?: google.maps.LatLngLiteral;
        /** A function to handle when the button is clicked. */
        locateMeHandler: (clientPos: google.maps.LatLngLiteral) => any;
    }>(),
    {
        initialPan: false,
        panOnUpdate: false,
    }
);

function locateMe() {
    props.locateMeHandler(props.clientPos);
}

onMounted(() => {
    if (props.clientPos != null) locateMe();
});
</script>

<template>
    <Button
        severity="secondary"
        :disabled="clientPos == null"
        @click="locateMe"
    >
        <template #icon>
            <MdiIcon :icon="mdiCrosshairsGps" />
        </template>
    </Button>
</template>
