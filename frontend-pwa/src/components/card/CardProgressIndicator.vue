<script setup lang="ts">
import ProgressBar, { ProgressBarProps } from "primevue/progressbar";
const props = defineProps<ProgressBarProps>();
</script>

<template>
    <div class="contents">
        <ProgressBar class="h-0.5 absolute top-0 left-0 right-0 bg-transparent" v-bind="props" />
        <ProgressBar class="h-0.5 absolute -bottom-px left-0 right-0 bg-transparent" v-bind="props" />
    </div>
</template>