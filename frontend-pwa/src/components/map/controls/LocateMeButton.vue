<script setup lang="ts">
import { mdiCrosshairsGps } from '@mdi/js';
import { onMounted } from 'vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import Button from 'primevue/button';
import { Position } from 'geojson';

const props = withDefaults(
    defineProps<{
        /** Whether the `locateMeHandler` should be called on mounted. @default true */
        initialPan?: boolean;
        panOnUpdate?: boolean;
        clientPos?: Position;
    }>(),
    {
        initialPan: false,
        panOnUpdate: false,
    }
);

const emit = defineEmits<{
    click: [Position]
}>()

onMounted(() => {
    if (props.clientPos) emit('click', props.clientPos);
});
</script>

<template>
    <Button
        severity="secondary"
        :disabled="!clientPos"
        @click="emit('click', clientPos!)"
    >
        <template #icon>
            <MdiIcon :icon="mdiCrosshairsGps" />
        </template>
    </Button>
</template>
