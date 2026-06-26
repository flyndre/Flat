<script setup lang="ts">
import { toLngLatLike } from '@/util/geoUtils';
import { useRafFn, watchImmediate } from '@vueuse/core';
import { Position } from 'geojson';
import { Map } from 'maplibre-gl';
import { onMounted, onUnmounted, ref, useTemplateRef } from 'vue';
import { getStyle } from '@/util/mapStyleUtils';
import { useTheme } from '@/plugins/ThemePlugin';
import { useToast } from 'primevue/usetoast';
import { useI18n } from 'vue-i18n';
import { TOAST_LIFE_LONG } from '@/data/constants';

const props = defineProps<{
    zoom: number;
    center: Position;
}>();

const loading = ref(true);
const mapContainer = useTemplateRef<HTMLDivElement>('map-container');
const { activeTheme } = useTheme();
const { t } = useI18n();
const { add } = useToast();

onMounted(async () => {
    const m = new Map({
        container: mapContainer.value!,
        pitch: 45,
        centerClampedToGround: true,
        style: getStyle('terrain', activeTheme.value),
    });

    watchImmediate(() => props.zoom, (z) => {
        m.setZoom(z);
    });

    watchImmediate(() => props.center, (c) => {
        m.setCenter(toLngLatLike(c));
    });

    onUnmounted(() => {
        m.remove();
    });

    const { unsubscribe } = m.on('error', () => {
        add({
            severity: 'warn',
            summary: t('components.cinematic_map.initialization_failed'),
            detail: t('components.cinematic_map.initialization_failed_text'),
            life: TOAST_LIFE_LONG,
        });
        unsubscribe(); // Only show error once.
    });

    await new Promise((r) => m.on('load', r));

    useRafFn(({ timestamp }) => {
        m.rotateTo((timestamp / 200) % 360, { duration: 0 });
    });

    loading.value = false;
});
</script>

<template>
    <div ref="map-container" class="min-w-[150vmax] min-h-[150vmax] blur-[1px] transition-opacity duration-1000" :class="{ 'opacity-0': loading }"></div>
    <div
        v-if="!loading"
        class="!z-10 fixed -top-[10%] -left-[10%] -right-[10%] -bottom-[10%] backdrop-blur c-tilt-shift-filter transition-opacity duration-1000"
    ></div>
</template>

<style scoped>
.c-animate-areal {
    --transform-base: scale(2) perspective(35rem) rotateX(45deg)
        translateZ(1.8vh);
    animation: c-areal-animation calc(2 * 60s) linear infinite;
}

@keyframes c-areal-animation {
    from {
        transform: var(--transform-base) rotateZ(0deg);
    }
    to {
        transform: var(--transform-base) rotateZ(360deg);
    }
}

.c-tilt-shift-filter {
    background: linear-gradient(
        rgba(0, 133, 221, 0.1) 0,
        rgba(0, 109, 182, 0.05) 30%,
        transparent 45%,
        transparent 60%,
        rgba(71, 0, 88, 0.1) 90%,
        rgba(51, 0, 40, 0.2) 100%
    );
    mask: linear-gradient(
            black 0,
            rgba(0, 0, 0, 0.9) 35%,
            rgba(0, 0, 0, 0.5) 40%,
            transparent 45%,
            transparent 60%,
            rgba(0, 0, 0, 0.5) 62%,
            rgba(0, 0, 0, 0.9) 75%,
            black 100%
        )
        alpha;
}
</style>
