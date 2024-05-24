// db.ts
import { Collection } from '@/types/Collection';
import { CollectionStats } from '@/types/stats/CollectionStats';
import { TrackingLog } from '@/types/TrackingLog';
import Dexie, { Table } from 'dexie';

export class FlatDexie extends Dexie {
    // 'collections' is added by dexie when declaring the stores()
    // We just tell the typing system this is the case
    collections!: Table<Collection, string>;
    collectionStats!: Table<CollectionStats, string>;
    trackingLogs!: Table<TrackingLog, string>;

    constructor() {
        super('FlatDatabase');
        this.version(10).stores({
            collections: '++id, name, adminClientId, area, divisions', // Primary key and indexed props
            collectionStats: '++id, collectionId, startDate, finishDate',
            trackingLogs: '++timestamp, tackingId, position',
        });
    }
}

export default new FlatDexie();
