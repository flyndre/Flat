import { TypedOverlay } from '@/types/map/TypedOverlay';
import { Ref, computed } from 'vue';

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

export function getShapeBounds(shape: TypedOverlay) {
    if (
        ((overlay): overlay is google.maps.Rectangle =>
            shape.type === google.maps.drawing.OverlayType.RECTANGLE)(
            shape.overlay
        )
    ) {
        return shape.overlay.getBounds();
    }
    if (
        ((overlay): overlay is google.maps.Circle =>
            shape.type === google.maps.drawing.OverlayType.CIRCLE)(
            shape.overlay
        )
    ) {
        return shape.overlay.getBounds();
    }
    if (
        ((overlay): overlay is google.maps.Polygon =>
            shape.type === google.maps.drawing.OverlayType.POLYGON)(
            shape.overlay
        )
    ) {
        return latLngArrayToBounds(shape.overlay.getPath().getArray());
    }
    if (
        ((overlay): overlay is google.maps.Polyline =>
            shape.type === google.maps.drawing.OverlayType.POLYLINE)(
            shape.overlay
        )
    ) {
        return latLngArrayToBounds(shape.overlay.getPath().getArray());
    }
}

export function latLngArrayToBounds(latLngArray: google.maps.LatLng[]) {
    return latLngArray.reduce(
        (p, c, _i) => p.extend(c),
        new google.maps.LatLngBounds()
    );
}
