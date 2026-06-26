import { TRACKING_INTERVAL } from '@/data/constants';
import { logPosition, trackingLogDB } from '@/data/trackingLogs';
import { useSanitizedGeolocation } from '@/util/geoLocationUtils';
import { useIntervalFn } from '@vueuse/core';
import { computed, ref } from 'vue';

let currentTrackId: string | undefined;

const {
    coords,
    error: geolocationError,
    pause: pauseGeolocation,
    resume: resumeGeolocation,
} = useSanitizedGeolocation({
    enableHighAccuracy: true,
});

const _errorOverride = ref<{
    code: number;
    message: string;
}>();
const error = computed(() => _errorOverride.value ?? geolocationError.value);

const {
    isActive,
    pause: pauseInterval,
    resume: resumeInterval,
} = useIntervalFn(
    () => {
        if (error.value && error.value.code > 0) {
            stop();
            return;
        }
        if (currentTrackId === undefined) {
            _errorOverride.value = {
                code: 4,
                message: 'currentTrackId is undefined',
            };
            stop();
            return;
        }
        if (coords.value === undefined) {
            _errorOverride.value = {
                code: 3,
                message: 'Invalid geolocation',
            };
            stop();
            return;
        }
        logPosition([coords.value.longitude, coords.value.latitude], currentTrackId);
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
    currentTrackId = crypto.randomUUID();
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
