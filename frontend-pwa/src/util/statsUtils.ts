import { ActiveCollection } from '@/types/ActiveCollection';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { CollectionStats } from '@/types/stats/CollectionStats';
import area from '@turf/area';
import length from '@turf/length';
import { v4 as uuidv4 } from 'uuid';
import { dbSafe } from './dbUtils';

export function getGeoJsonArea(geometry: GeoJSON.Geometry) {
    return (area(geometry) ?? 0) / 1000000;
}

export function getGeoJsonLength(
    geometry: GeoJSON.LineString | GeoJSON.MultiLineString
) {
    return length(geometry);
}

function _getParticipantStats(participant: ParticipantTrack) {
    const tracks: GeoJSON.LineString = {
        type: 'LineString',
        coordinates: participant.progress
            .map((t) => t.track.coordinates)
            .flat(),
    };
    console.log(tracks);
    return {
        ...participant,
        coveredDistance:
            tracks.coordinates?.length > 1 ? getGeoJsonLength(tracks) : 0,
    };
}

export function calculateCollectionStats(
    collection: ActiveCollection,
    startDate: Date = undefined
): CollectionStats {
    const divisionStats = collection.divisions.map((d) => ({
        ...d,
        coveredArea: getGeoJsonArea(d.area),
    }));
    const participantStats = collection.confirmedUsers.map((u) =>
        _getParticipantStats(dbSafe(u))
    );
    return {
        id: uuidv4(),
        collectionId: collection.id,
        startDate,
        finishDate: new Date(),
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
