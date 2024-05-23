import { CollectionStats } from '@/types/stats/CollectionStats';
import { useObservable } from '@vueuse/rxjs';
import { liveQuery } from 'dexie';
import db from './db';

export const collectionStatsDB = db.collectionStats;

export function statsOf(collectionId: string) {
    return useObservable<CollectionStats[]>(
        /* @ts-ignore */
        liveQuery(() => db.collectionStats.where({ collectionId }).toArray())
    );
}
