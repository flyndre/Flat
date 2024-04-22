import { Collection } from '@/types/Collection';

export default (collection: Collection) =>
    ![
        collection.adminClientId !== undefined,
        collection.adminClientId?.length > 0,
        collection.name !== undefined,
        collection.name?.length > 0,
        collection.area !== undefined,
        collection.area?.coordinates !== undefined,
    ].includes(false);
