<script setup lang="ts">
import CustomMap from '@/components/map/CustomMap.vue';
import ProgressSpinner from 'primevue/progressspinner';
</script>

<template>
    <Suspense>
        <CustomMap />
        <template #fallback>
            <div
                class="w-full h-full flex flex-col justify-center items-center"
            >
                <ProgressSpinner />
            </div>
        </template>
    </Suspense>
</template>
