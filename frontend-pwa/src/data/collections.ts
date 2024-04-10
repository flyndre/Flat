import { Collection } from '@/types/collection';
import { useObservable } from '@vueuse/rxjs';
import { liveQuery } from 'dexie';
import db from './db';

export const collections = useObservable<Collection[]>(
    /** @ts-ignore */
    liveQuery(() => db.collections.toArray())
);

export const collectionService = db.collections;
