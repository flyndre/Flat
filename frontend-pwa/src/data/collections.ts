import { Collection } from '@/types/Collection';
import { useObservable } from '@vueuse/rxjs';
import { liveQuery } from 'dexie';
import db from './db';

export const collections = useObservable<Collection[]>(
    /** @ts-ignore */
    liveQuery(() => db.collections.toArray())
);

export const collectionService = db.collections;

export const collectionDraft = {
    get: (): Collection => JSON.parse(localStorage.getItem('collectionDraft')),
    set: (v: Collection) =>
        localStorage.setItem('collectionDraft', JSON.stringify(v)),
};
