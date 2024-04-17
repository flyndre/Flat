import { IdentifyableTypedOverlay } from '@/types/map/IdentifyableTypedOverlay';
import { OverlayOptions } from '@/types/map/OverlayOptions';
import { TypedOverlay } from '@/types/map/TypedOverlay';
import { Geometry } from 'geojson';
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

export function getShapeColor(shape: IdentifyableTypedOverlay | TypedOverlay) {
    return <string>(
        (shape?.overlay?.get('fillColor') ?? shape?.overlay?.get('strokeColor'))
    );
}

export function getShapeListBounds(shapes: TypedOverlay[]) {
    return shapes.reduce(
        (p, c, _i) => p.union(getShapeBounds(c)),
        new google.maps.LatLngBounds()
    );
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

/**
 * Creates a frame that contains all given location points.
 * @param latLngArray an array of `LatLng` tuples
 * @returns a `LatLngBounds` object that frames all points
 */
export function latLngArrayToBounds(latLngArray: google.maps.LatLng[]) {
    return latLngArray.reduce(
        (p, c, _i) => p.extend(c),
        new google.maps.LatLngBounds()
    );
}

export function shapeToGeoJSON(shape: TypedOverlay) {
    const overlayType = google.maps.drawing.OverlayType;
    switch (shape.type ?? shape.overlay?.type) {
        case overlayType.POLYGON:
            return polygonToGeoJSON(<google.maps.Polygon>shape.overlay);
        case overlayType.POLYLINE:
            return polylineToGeoJSON(<google.maps.Polyline>shape.overlay);
        case overlayType.RECTANGLE:
        case overlayType.CIRCLE:
        default:
            return undefined;
    }
}

export function polylineToGeoJSON(polyline: google.maps.Polyline) {
    const geometry: GeoJSON.LineString = {
        type: 'LineString',
        coordinates: _coordinatesFromPolything(polyline),
    };
    return geometry;
}

export function polygonToGeoJSON(polygon: google.maps.Polygon) {
    const geometry: GeoJSON.Polygon = {
        type: 'Polygon',
        coordinates: [_coordinatesFromPolything(polygon)],
    };
    return geometry;
}

/**
 * Retrieves each node's coordinates of the given Google Maps overlay.
 * @see https://stackoverflow.com/a/76009320/11793652
 * @param polything a `google.maps.Polygon` or `google.maps.Polyline` object
 * @returns a GeoJSON `Position` array
 */
function _coordinatesFromPolything(
    polything: google.maps.Polygon | google.maps.Polyline
): GeoJSON.Position[] {
    return polything
        .getPath()
        .getArray()
        .map((vertex) => [vertex.lat(), vertex.lng()]);
}

export function geoJSONtoShape(
    geoJSON: Geometry,
    shapeOptions: google.maps.GroundOverlayOptions = {}
) {
    switch (geoJSON.type) {
        case 'Polygon':
            return geoJSONtoPolygon(geoJSON, shapeOptions);
        case 'LineString':

        default:
            return undefined;
    }
}

export function geoJSONtoPolygon(
    geoJSON: GeoJSON.Polygon,
    shapeOptions: OverlayOptions = {}
) {
    return new google.maps.Polygon({
        ...shapeOptions,
        paths: [
            geoJSON.coordinates[0].map((coords) => ({
                lat: coords[0],
                lng: coords[1],
            })),
        ],
    });
}
