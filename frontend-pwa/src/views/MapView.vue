<script setup lang="ts">
import Card from "primevue/card";
import MapWithControls from "@/components/map/MapWithControls.vue";
</script>

<template>
    <Card>
        <template #content>
            <MapWithControls />
        </template>
    </Card>
</template>
