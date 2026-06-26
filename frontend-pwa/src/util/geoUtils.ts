import { Division } from "@/types/Division";
import { Position } from "geojson";
import { LngLat, LngLatBounds, LngLatBoundsLike, LngLatLike } from "maplibre-gl";

export function toPosition(lngLatLike: LngLatLike): Position {
    if (Array.isArray(lngLatLike)) {
        return lngLatLike
    }
    if (lngLatLike instanceof LngLat) {
        return lngLatLike.toArray()
    }
    if ('lon' in lngLatLike) {
        return [lngLatLike.lon, lngLatLike.lat]
    }
    return [lngLatLike.lng, lngLatLike.lat]
}

export function toLngLatLike(position: Position): LngLatLike {
    const [lng, lat] = position
    return {
        lng,
        lat
    }
}

export function getDivisionBounds(division: Division): LngLatBoundsLike {
    const bounds = new LngLatBounds();
    for (const p of division.area.coordinates.flat()) {
        bounds.extend(toLngLatLike(p))
    }
    return bounds
}

export function getDivisionsBounds(divisions: Division[]): LngLatBoundsLike {
    const bounds = new LngLatBounds();
    for (const d of divisions) {
        bounds.extend(getDivisionBounds(d))
    }
    return bounds
}

export function toMultiPolygon(bounds: LngLatBoundsLike): GeoJSON.MultiPolygon {
    const b = bounds instanceof LngLatBounds ? bounds : new LngLatBounds(bounds)
    return {
        type: 'MultiPolygon',
        coordinates: [[b.toArray()]]
    }
}
