<script setup lang="ts">
import InputIcon from "primevue/inputicon";
import MdiIcon from "@/components/MdiIcon.vue";

defineProps<{
    icon: string;
}>();
</script>

<template>
    <InputIcon>
        <MdiIcon class="mb-0.5 ml-1" :icon />
    </InputIcon>
</template>
