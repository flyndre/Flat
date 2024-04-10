import api from "./api";


export async function openCollection(name : string, clientId : string, collectionArea : any){
    const response = await api.post("collection", {name : name, clientId : clientId, collectionArea : collectionArea}); 
    return response; 
}

export async function changeCollectionArea(collectionId : string, collectionArea : any[]){
    const response = await api.put(`collection/${collectionId}`, {value: collectionArea}); 
    return response;
}

export async function deleteCollection(collectionId : string, collectionArea : any[]){
    const response = await api.delete(`collection/${collectionId}`); 
    return response;
}

export async function accessRequest(username : string, clientId : string, collectionId : string){
    const response = await api.post(`collection/${collectionId}`, {value: clientId}); 
    return response;
}