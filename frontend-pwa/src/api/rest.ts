import api from "./api";

/**
 * opens the collection.
 * 
 * @param name of the collection
 * @param clientId of the owner
 * @param collectionArea as the Area in witch the collection is taking place
 * @returns The Response of the Request
 */
export async function openCollection(name : string, clientId : string, collectionArea : any){
    const response = await api.post("collection", {name : name, clientId : clientId, collectionArea : collectionArea}); 
    return response; 
}

/**
 * change the collection Area of a given Collection.
 * 
 * @param collectionId of the collection that is going to be changed
 * @param collectionArea as the new Area in witch the collection is taking place
 * @returns 
 */
export async function changeCollectionArea(collectionId : string, collectionArea : any[]){
    const response = await api.put(`collection/${collectionId}`, {value: collectionArea}); 
    return response;
}

/**
 * delete the given Collection
 * 
 * @param collectionId of the collection that is going to be deleted
 * @returns 
 */
export async function deleteCollection(collectionId : string){
    const response = await api.delete(`collection/${collectionId}`); 
    return response;
}

/**
 * access Request to a Collection. The Request is forwated to the Owner who is accepting or declining the Accessrequest.
 * 
 * @param username of wich the User want to be displayed
 * @param clientId of the User
 * @param collectionId of the Collection
 * @returns 
 */
export async function accessRequest(username : string, clientId : string, collectionId : string){
    const response = await api.post(`collection/${collectionId}`, {value: clientId}); 
    return response;
}