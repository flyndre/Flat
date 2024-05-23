import { ActiveCollection } from '@/types/ActiveCollection';
import { CollectionStats } from '@/types/stats/CollectionStats';
import geoJsonArea from '@mapbox/geojson-area';
import geoJsonLength from 'geojson-length';
import { v4 as uuidv4 } from 'uuid';

export function getGeoJsonArea(geometry: GeoJSON.Geometry) {
    return geoJsonArea.geometry(geometry);
}

export function getGeoJsonLength(
    geometry: GeoJSON.LineString | GeoJSON.MultiLineString
) {
    return geoJsonLength(geometry);
}

export function calculateCollectionStats(
    collection: ActiveCollection
): CollectionStats {
    const divisionStats = collection.divisions.map((d) => ({
        ...d,
        coveredArea: getGeoJsonArea(d.area),
    }));
    const participantStats = collection.confirmedUsers.map((u) => ({
        ...u,
        coveredDistance: getGeoJsonLength({
            type: 'MultiLineString',
            coordinates: u.progress.map((t) => t.track.coordinates),
        }),
    }));
    return {
        id: uuidv4(),
        collectionId: collection.id,
        name: collection.name,
        admin: collection.confirmedUsers.find(
            ({ id }) => id === collection.adminClientId
        ),
        divisionStats,
        participantStats,
        converedArea: divisionStats
            .map((ds) => ds.coveredArea)
            .reduce((a, b) => a + b, 0),
    };
}
