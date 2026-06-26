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
    get: (): Collection | undefined => {
        const item = localStorage.getItem('collectionDraft');
        if (item === null) {
            return undefined;
        }
        try {
            return JSON.parse(item);
        } catch (e) {
            localStorage.removeItem('collectionDraft');
            return undefined;
        }
    },
    set: (v: Collection | undefined) => {
        if (v === undefined) {
            localStorage.removeItem('collectionDraft');
        }
        localStorage.setItem('collectionDraft', JSON.stringify(v))
    }
};

export const lastActiveCollection = {
    get: (): ActiveCollection | undefined => {
        const item = localStorage.getItem('lastActiveCollection');
        if (item === null) {
            return undefined;
        }
        try {
            return JSON.parse(item);
        } catch (e) {
            localStorage.removeItem('lastActiveCollection');
            return undefined;
        }
    },
    set: (v: ActiveCollection | undefined) => {
        if (v === undefined) {
            localStorage.removeItem('lastActiveCollection');
        }
        localStorage.setItem('lastActiveCollection', JSON.stringify(v))
    },
};

export function autoUpdateLastActiveCollection(
    activeCollectionRef: Ref<ActiveCollection>
) {
    return watch(activeCollectionRef, (v) => lastActiveCollection.set(v));
}
