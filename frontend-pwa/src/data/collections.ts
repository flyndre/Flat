import { ActiveCollection } from '@/types/ActiveCollection';
import { Collection } from '@/types/Collection';
import { useObservable } from '@vueuse/rxjs';
import { liveQuery } from 'dexie';
import { Ref, watch } from 'vue';
import db from './db';

export const collections = useObservable<Collection[]>(
    /** @ts-ignore */
    liveQuery(() => db.collections.toArray())
);

export const collectionDB = db.collections;

export const collectionDraft = {
    get: (): Collection => JSON.parse(localStorage.getItem('collectionDraft')),
    set: (v: Collection) =>
        localStorage.setItem('collectionDraft', JSON.stringify(v)),
};

export const lastActiveCollection = {
    get: (): ActiveCollection =>
        JSON.parse(localStorage.getItem('lastActiveCollection')),
    set: (v: ActiveCollection) =>
        localStorage.setItem('lastActiveCollection', JSON.stringify(v)),
};

export function autoUpdateLastActiveCollection(
    activeCollectionRef: Ref<ActiveCollection>
) {
    return watch(activeCollectionRef, (v) => lastActiveCollection.set(v));
}
