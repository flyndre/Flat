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
): GeoJSON.MultiPolygon {
    const areaBounds = getShapeListBounds(shapeList).toJSON();
    return {
        type: 'MultiPolygon',
        coordinates: [
            [
                [
                    [areaBounds.west, areaBounds.north],
                    [areaBounds.east, areaBounds.north],
                    [areaBounds.east, areaBounds.south],
                    [areaBounds.west, areaBounds.south],
                    [areaBounds.west, areaBounds.north],
                ],
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
            type: 'Polygon',
            coordinates: polygonToGeoJSON(<google.maps.Polygon>shape.overlay)
                .coordinates,
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
                coordinates: division.area.coordinates,
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
        name: shapeList?.[0]?.name?.split('_#')?.[0],
        color: getShapeColor(shapeList?.[0]),
        progress: shapeList.map((s) => ({
            id: s.name?.split('_#')?.[1],
            track: polylineToGeoJSON(<google.maps.Polyline>s.overlay),
        })),
    };
}

export function trackToShapeList(
    track: ParticipantTrack,
    shapeOptions: OverlayOptions = {}
): IdentifyableTypedOverlay[] {
    return track.progress.map((p, i) => ({
        id: p.id,
        name: `${track.name}_#${i}`,
        type: <google.maps.drawing.OverlayType>'polyline',
        overlay: geoJSONtoPolyline(p.track, shapeOptions),
    }));
}
