import { TrackingLog } from '@/types/TrackingLog';
import { useObservable } from '@vueuse/rxjs';
import { liveQuery } from 'dexie';
import db from './db';

export const trackingLogs = useObservable<TrackingLog[]>(
    /** @ts-ignore */
    liveQuery(() => db.trackingLogs.toArray())
);

export const trackingLogDB = db.trackingLogs;

export function logPosition(position: GeoJSON.Position) {
    try {
        db.trackingLogs.add({ timestamp: Date.now(), position });
    } catch (error) {
        console.log('Failed to log position:', error);
    }
}
