<script setup lang="ts">
import MdiIcon from '@/components/icons/MdiIcon.vue';

defineProps<{
    icon: string;
}>();
</script>

<template>
    <MdiIcon class="mr-2.5" :icon />
</template>
