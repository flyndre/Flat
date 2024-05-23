import { Collection } from '@/types/Collection';
import { Division } from '@/types/Division';
import api from './api';

/**
 * opens the collection.
 *
 * @param name of the collection
 * @param clientId of the owner
 * @param collectionArea as the Area in witch the collection is taking place
 * @returns the created Collection
 */
export async function openCollection(collection: Collection) {
    var collectionDto = collection as any;
    collectionDto.clientId = collection.adminClientId;
    collectionDto.collectionDivision = collection.divisions;
    delete collectionDto.divisions;
    delete collectionDto.adminClientId;
    const response = await api.post('api/rest/collection', collectionDto);
    return response;
}

/**
 * get the requested collection.
 *
 * @param collectionId of the collection
 * @returns the requested collection
 */
export async function getCollection(collectionId: string, clientId: string) {
    const response = await api.get(
        `api/rest/collection/${collectionId}?userId=${clientId}`
    );
    return response;
}

/**
 * divides the collection Area into smaller Fractions.
 *
 * @param collectionId of the collection that is going to be changed
 * @param collectionArea as the new Area in witch the collection is taking place
 * @returns
 */
export async function divideCollectionArea(
    collectionId: string,
    collectionAreas: Division[]
) {
    const response = await api.put(
        `api/rest/collection/${collectionId}`,
        collectionAreas
    );
    return response;
}

/**
 * delete the given Collection
 *
 * @param collectionId of the collection that is going to be deleted
 * @returns
 */
export async function deleteCollection(collectionId: string) {
    const response = await api.delete(`api/rest/collection/${collectionId}`);
    return response;
}

/**
 * access Request to a Collection. The Request is forwarded to the Owner who is accepting or declining the Access-Request.
 *
 * @param username of which the User want to be displayed
 * @param clientId of the User
 * @param collectionId of the Collection
 * @returns True or False
 */
export async function accessRequest(
    username: string,
    clientId: string,
    collectionId: string
) {
    const response = await api.post(`api/rest/accessrequest/${collectionId}`, {
        username: username,
        clientId: clientId,
    });
    return response;
}

/**
 * access Request to a Collection. The Request is forwarded to the Owner who is accepting or declining the Access-Request.
 *
 * @param username of which the User want to be displayed
 * @param clientId of the User
 * @param collectionId of the Collection
 * @returns True or False
 */
export async function confirmRequest(
    username: string,
    clientId: string,
    accepted: boolean,
    collectionId: string
) {
    const response = await api.post(
        `api/rest/AccessConfirmation/${collectionId}`,
        {
            username: username,
            clientId: clientId,
            accepted: accepted,
        }
    );
    return response;
}

/**
 * Leave a collection as a participant. Admins should end the collection rather than leaving it.
 *
 * @param clientId of the User
 * @param collectionId of the Collection
 * @returns idk
 */
export async function leaveCollection(clientId: string, collectionId: string) {
    const response = await api.post(
        `api/rest/LeaveCollection/${collectionId}`,
        {
            clientId: clientId,
        }
    );
    return response;
}

export async function leaveCollection(
    collectionId: string,
    clientId: string
){
    const response = await api.post(`api/rest/LeaveCollection/${collectionId}?clientId=${clientId}`);
    return response;
}

export async function kickUser(
    collectionId: string,
    clientId: string,
    adminId: string
){
    const response = await api.post(`api/rest/RemoveUser/${collectionId}?clientId=${clientId}&bossId=${adminId}`);
    return response;
}

