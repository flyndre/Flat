<script setup lang="ts">
defineProps<{
    icon: string;
}>();
</script>

<template>
    <svg
        class="opacity-85 scale-[2] shrink-0"
        width="10"
        height="10"
        viewBox="0 0 24 24"
    >
        <path class="fill-current" :d="icon" />
    </svg>
</template>
