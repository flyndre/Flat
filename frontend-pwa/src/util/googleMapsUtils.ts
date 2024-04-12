import { computed, Ref } from 'vue';

export function ifInfinityReplaceWith(number: number, replacement: number) {
    return Math.abs(number) === Number.POSITIVE_INFINITY ? replacement : number;
}

export function safeMapCenterFromGeolocationCoords(
    coords: Ref<GeolocationCoordinates>
) {
    return computed(() => ({
        lat: ifInfinityReplaceWith(coords.value.latitude, 0),
        lng: ifInfinityReplaceWith(coords.value.longitude, 0),
    }));
}
