import { TRACKING_INTERVAL } from '@/data/constants';
import { logPosition } from '@/data/trackingLogs';
import { useGeolocation, useIntervalFn } from '@vueuse/core';
import { computed, ref } from 'vue';

const {
    coords,
    error: geolocationError,
    pause: pauseGeolocation,
    resume: resumeGeolocation,
} = useGeolocation({
    enableHighAccuracy: true,
});

const _errorOverride = ref<{
    code: number;
    message: string;
}>(null);
const error = computed(() => _errorOverride.value ?? geolocationError.value);

const {
    isActive,
    pause: pauseInterval,
    resume: resumeInterval,
} = useIntervalFn(
    () => {
        if (error.value != null && error.value.code > 0) {
            stop();
            return;
        }
        const position = [coords.value.latitude, coords.value.longitude];
        if (position.includes(null) || position.includes(undefined)) {
            _errorOverride.value = {
                code: 3,
                message: 'One or more coordinates were null: ' + position,
            };
            stop();
            return;
        }
        logPosition(position);
    },
    TRACKING_INTERVAL,
    {
        immediate: false,
    }
);

function stop() {
    pauseInterval();
    pauseGeolocation();
}

function start() {
    resumeGeolocation();
    resumeInterval();
}

export const useTrackingService = () => ({
    start,
    stop,
    isActive,
    error,
    coords,
});