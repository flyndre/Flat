import axios from 'axios'

axios.defaults.baseURL = import.meta.env.BASE_URL;

export async function openCollection(name : string, clientId : string, collectionArea : any){
    
    const response = await axios.post("collection", {name : name, clientId : clientId, collectionArea : collectionArea}); 
    const responseData = response.status != 200 ? null : response.data;
    
    if(responseData == null){
        throw WebTransportError; 
    }

    return responseData; 
}

export async function accessRequest(username : string, clientId : string, collectionId : string){
    
}

export async function getCollection(collectionId : string){
    
}