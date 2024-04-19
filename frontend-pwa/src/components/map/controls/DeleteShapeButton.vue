<script setup lang="ts">
import { mdiDeleteForever } from '@mdi/js';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import Button from 'primevue/button';

const props = defineProps<{
    shapeSelected: boolean;
    deleteShapeHandler: () => any;
}>();
</script>

<template>
    <Button
        class="shrink-0"
        severity="secondary"
        :disabled="!shapeSelected"
        @click="deleteShapeHandler"
    >
        <template #icon>
            <MdiIcon :icon="mdiDeleteForever" />
        </template>
    </Button>
</template>
