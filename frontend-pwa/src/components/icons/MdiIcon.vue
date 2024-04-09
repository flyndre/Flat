<script setup lang="ts">
import SvgIcon from '@jamescoyle/vue-icon';
defineProps<{
    icon: string;
}>();
</script>

<template>
    <SvgIcon
        class="opacity-85 mdi-icon"
        style="transform: scale(2)"
        type="mdi"
        size="10"
        :path="icon"
    />
</template>
