import { Collection } from '@/types/collection';

export default (collection: Collection) =>
    ![
        collection.adminClientId !== undefined,
        collection.adminClientId.length > 0,
        collection.name !== undefined,
        collection.name.length > 0,
    ].includes(false);
