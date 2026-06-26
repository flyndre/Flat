<script setup lang="ts">
import { mdiFitToScreen } from '@mdi/js';
import Button from 'primevue/button';
import { onMounted } from 'vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';

const props = withDefaults(
    defineProps<{
        shapesPresent: boolean;
        /** Whether the `locateMeHandler` should be called on mounted. @default true */
        initialPan?: boolean;
    }>(),
    {
        initialPan: true,
    }
);

const emit = defineEmits<{
    click: []
}>()

onMounted(() => {
    if (props.shapesPresent) {
        emit('click')
    }
});
</script>

<template>
    <Button
        severity="secondary"
        :disabled="!shapesPresent"
        @click="emit('click')"
    >
        <template #icon>
            <MdiIcon :icon="mdiFitToScreen" />
        </template>
    </Button>
</template>
