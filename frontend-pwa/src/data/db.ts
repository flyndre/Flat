// db.ts
import { Collection } from '@/types/Collection';
import Dexie, { Table } from 'dexie';

export class FlatDexie extends Dexie {
    // 'collections' is added by dexie when declaring the stores()
    // We just tell the typing system this is the case
    collections!: Table<Collection, string>;

    constructor() {
        super('FlatDatabase');
        this.version(3).stores({
            collections: '++id, name, adminClientId, area, divisions', // Primary key and indexed props
        });
    }
}

export default new FlatDexie();
