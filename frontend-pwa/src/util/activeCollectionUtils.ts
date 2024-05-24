import { ActiveCollection } from '@/types/ActiveCollection';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { TrackInstance } from '@/types/TrackInstance';

export function mergeActiveCollections(
    from: ActiveCollection,
    to: ActiveCollection
) {
    const merged = copyMissingProperties(from, to);
    // Deep merge tracks
    from.confirmedUsers.forEach((pFrom) => {
        const index = merged.confirmedUsers.findIndex(
            (pTo) => pTo.id === pFrom.id
        );
        if (index === -1) {
            merged.confirmedUsers.push(pFrom);
            return;
        }
        merged[index] = mergeParticipantTracks(pFrom, merged[index]);
    });
    return merged;
}

export function mergeParticipantTracks(
    from: ParticipantTrack,
    to: ParticipantTrack
) {
    if (to == null || from == null) return from;
    const merged = copyMissingProperties(from, to);
    const mergedProgress = new Map<string, TrackInstance>();
    from.progress.forEach((t) => mergedProgress.set(t.id, t));
    to.progress.forEach((t) => mergedProgress.set(t.id, t));
    merged.progress = [...mergedProgress.values()];
    return merged;
}

export function copyMissingProperties<T = object>(from: T, to: T) {
    const entriesFrom = Object.entries(from);
    entriesFrom.forEach(([key, value]) => {
        to[key] = to[key] ?? value;
    });
    return to;
}
