import { Division } from '@/types/Division';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { IdentifyableTypedOverlay } from '@/types/map/IdentifyableTypedOverlay';
import { OverlayOptions } from '@/types/map/OverlayOptions';
import {
    geoJSONtoPolygon,
    geoJSONtoPolyline,
    getShapeColor,
    getShapeListBounds,
    polygonToGeoJSON,
    polylineToGeoJSON,
} from './googleMapsUtils';

export function areaFromShapeList(
    shapeList: IdentifyableTypedOverlay[]
): GeoJSON.Polygon {
    const areaBounds = getShapeListBounds(shapeList).toJSON();
    return {
        type: 'Polygon',
        coordinates: [
            [
                [areaBounds.north, areaBounds.west],
                [areaBounds.north, areaBounds.east],
                [areaBounds.south, areaBounds.east],
                [areaBounds.south, areaBounds.west],
                [areaBounds.north, areaBounds.west],
            ],
        ],
    };
}

export function shapeToDivision(shape: IdentifyableTypedOverlay): Division {
    return {
        id: shape.id,
        name: shape.name,
        color: getShapeColor(shape),
        area: {
            type: 'MultiPolygon',
            coordinates: [
                polygonToGeoJSON(<google.maps.Polygon>shape.overlay)
                    .coordinates,
            ],
        },
    };
}

export function divisionToShape(
    division: Division,
    shapeOptions: OverlayOptions = {}
): IdentifyableTypedOverlay {
    return {
        id: division.id,
        name: division.name,
        type: <google.maps.drawing.OverlayType>'polygon',
        overlay: geoJSONtoPolygon(
            {
                type: 'Polygon',
                coordinates: division.area.coordinates?.[0],
            },
            {
                ...shapeOptions,
                editable: false,
                draggable: false,
                strokeColor: division.color,
                fillColor: division.color,
            }
        ),
    };
}

export function shapeListToTrack(
    shapeList: IdentifyableTypedOverlay[]
): ParticipantTrack {
    return {
        id: shapeList?.[0]?.id,
        name: shapeList?.[0]?.name,
        color: getShapeColor(shapeList?.[0]),
        progress: shapeList.map((s) =>
            polylineToGeoJSON(<google.maps.Polyline>s.overlay)
        ),
    };
}

export function trackToShapeList(
    track: ParticipantTrack,
    shapeOptions: OverlayOptions = {}
): IdentifyableTypedOverlay[] {
    return track.progress.map((p) => ({
        id: track.id,
        name: track.name,
        type: <google.maps.drawing.OverlayType>'polyline',
        overlay: geoJSONtoPolyline(p, shapeOptions),
    }));
}
