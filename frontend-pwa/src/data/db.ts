// db.ts
import { Collection } from '@/types/collection';
import Dexie, { Table } from 'dexie';

export class FlatDexie extends Dexie {
    // 'friends' is added by dexie when declaring the stores()
    // We just tell the typing system this is the case
    collections!: Table<Collection>;

    constructor() {
        super('FlatDatabase');
        this.version(1).stores({
            collections: '++id, name, adminClientId', // Primary key and indexed props
        });
    }
}

export default new FlatDexie();
