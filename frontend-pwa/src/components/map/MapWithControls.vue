<script setup lang="ts">
import CustomMap from '@/components/map/CustomMap.vue';
import ProgressSpinner from 'primevue/progressspinner';
</script>

<template>
    <Suspense>
        <CustomMap />
        <template #fallback>
            <ProgressSpinner />
        </template>
    </Suspense>
</template>
