import { Collection } from '@/types/Collection';

export function backupToCollectionList(backupData: string) {
    try {
        return <Collection[]>JSON.parse(atob(backupData));
    } catch (e) {
        return undefined;
    }
}

export function collectionListToBackup(collectionList: Collection[]) {
    try {
        return btoa(JSON.stringify(collectionList));
    } catch (e) {
        return undefined;
    }
}
