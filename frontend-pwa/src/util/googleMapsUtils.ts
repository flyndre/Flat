import { computed, Ref } from 'vue';

export function ifInfinityReplaceWith(number: number, replacement: number) {
    return Math.abs(number) === Number.POSITIVE_INFINITY ? replacement : number;
}

export function mapCenterWithDefaults(
    coords: Ref<GeolocationCoordinates>,
    defaults: {
        lat: number;
        lng: number;
    } = {
        lat: 0,
        lng: 0,
    }
) {
    return computed(() => ({
        lat: ifInfinityReplaceWith(coords.value.latitude, defaults.lat),
        lng: ifInfinityReplaceWith(coords.value.longitude, defaults.lng),
    }));
}
