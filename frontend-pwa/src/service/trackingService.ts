import { TRACKING_INTERVAL } from '@/data/constants';
import { logPosition, trackingLogDB } from '@/data/trackingLogs';
import { useGeolocation, useIntervalFn } from '@vueuse/core';
import { v4 as uuidv4 } from 'uuid';
import { computed, ref } from 'vue';

let currentTrackId = null;

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
        const position = [coords.value.longitude, coords.value.latitude];
        if (position.includes(null) || position.includes(undefined)) {
            _errorOverride.value = {
                code: 3,
                message: 'One or more coordinates were null: ' + position,
            };
            stop();
            return;
        }
        logPosition(position, currentTrackId);
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
    currentTrackId = uuidv4();
    resumeGeolocation();
    resumeInterval();
}

function reset() {
    trackingLogDB.clear();
}

export const useTrackingService = () => ({
    reset,
    start,
    stop,
    isActive,
    error,
    coords,
});
