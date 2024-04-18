import { Division } from '@/types/Division';
import { IdentifyableTypedOverlay } from '@/types/map/IdentifyableTypedOverlay';
import { OverlayOptions } from '@/types/map/OverlayOptions';
import {
    geoJSONtoPolygon,
    getShapeColor,
    getShapeListBounds,
    polygonToGeoJSON,
} from './googleMapsUtils';

export function areaFromShapeList(
    shapeList: IdentifyableTypedOverlay[]
): GeoJSON.Polygon {
    const areaBounds = getShapeListBounds(shapeList).toJSON();
    return {
        type: 'Polygon',
        coordinates: [
            [
                [areaBounds.west, areaBounds.north],
                [areaBounds.east, areaBounds.north],
                [areaBounds.east, areaBounds.south],
                [areaBounds.west, areaBounds.south],
                [areaBounds.west, areaBounds.north],
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
