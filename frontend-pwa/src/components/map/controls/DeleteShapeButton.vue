<script setup lang="ts">
import { mdiDeleteForever, mdiRedo, mdiUndo } from '@mdi/js';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import Button from 'primevue/button';
import ButtonGroup from 'primevue/buttongroup';

const props = defineProps<{
    selected: boolean;
}>();

const emit = defineEmits<{
    click: [],
    undo: [],
    redo: [],
}>()
</script>

<template>
    <ButtonGroup>
        <Button
            severity="secondary"
            :disabled="!selected"
            @click="emit('click')"
        >
            <template #icon>
                <MdiIcon :icon="mdiDeleteForever" />
            </template>
        </Button>
        <!-- <Button
            severity="secondary"
            @click="emit('undo')"
        >
            <template #icon>
                <MdiIcon :icon="mdiUndo" />
            </template>
        </Button>
        <Button
            severity="secondary"
            @click="emit('redo')"
        >
            <template #icon>
                <MdiIcon :icon="mdiRedo" />
            </template>
        </Button> -->
    </ButtonGroup>
</template>
