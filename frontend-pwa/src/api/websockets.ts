import { useIntervalFn } from '@vueuse/core';
import { LineString } from 'geojson';
import db from '../data/db';
import { useWebSocket } from '@vueuse/core'
import { ref, watch } from "vue";
import { clientId } from "@/data/clientMetadata";
import { ParticipantTrack } from "@/types/ParticipantTrack";
import { InviteMessage } from "@/types/websocket/InviteMessage";
import { UpdateCollectionMessage } from "@/types/websocket/UpdateCollectionMessage";
import { IncrementalTrackMessage } from "@/types/websocket/IncrementalTrackMessage";



export let members = ref([{id: clientId.value, name: "admin", color: "#f8fafc", progress : []}] as ParticipantTrack[]);
export let newInvite = ref([]);
export let isAdmin = true;
let latestSendTimestamp = null;

export function setAdmin(boo){
    isAdmin = boo
}

const { status, data, send, open, close } = useWebSocket('wss://flat.buhss.de/api/ws', {
    autoReconnect: true
  })
const {
    isActive,
    pause: pauseInterval,
    resume: resumeInterval,
} = useIntervalFn(
    () => {

        console.log("Sending Trackingpoints...")
        var lineStringOfPosition = {type: "LineString", coordinates: [[0,0]]} as LineString
        
        var newlatest = null;
        db.trackingLogs.toCollection().each(el => {(el.timestamp > latestSendTimestamp) || latestSendTimestamp == null ? lineStringOfPosition.coordinates.push(el.position) : null; newlatest = el.timestamp})
    
        latestSendTimestamp = newlatest;

        send(JSON.stringify({type: 1, trackId: "94042b6e-a317-499a-af3d-1d32e58cbbb2", track: lineStringOfPosition, clientId: clientId.value}))
    },
    7000
);

watch(data, data => {
    let websocketMsg = JSON.parse(data)
    switch(websocketMsg.type){
        case "AccessRequest":
            handleAccessRequest(websocketMsg as InviteMessage); 
            break;
        case "CollectionUpdate":
            handleCollectionUpdate(websocketMsg as UpdateCollectionMessage);
            break;
        case "IncrementalTrack":
            handleIncrementalTracks(websocketMsg as IncrementalTrackMessage);
            break;
    }
})

export function closeCollection(collectionId : string){
    const answer = {type:  3, collectionId : collectionId}
    send(JSON.stringify(answer))
}

export function acceptOrDeclineAccessRequest(choice : boolean, username : string, clientId : string, collectionId : string){
    const answer = {
        type:  "AccessRequest",
        collectionId: collectionId,
        clientId: clientId,
        username: username,
        accepted: choice
    }
    console.log("Sending AcceptOrDeclineRequest:")
    console.log(answer)

    send(JSON.stringify(answer));
    newInvite.value.shift(); 
}

export function establishWebsocket(clientId : string, collectionId : string){
    send(JSON.stringify({
        type: "WebsocketConnection", 
        clientId: clientId,
        collectionId: collectionId
    }))
    resumeInterval();
}

export function pauseSendingToServer(){
    pauseInterval();
}

export function resumeSendingToServer(){
    resumeInterval();
}

export function addMemberToCollection(clients : {clientId: string, username: string}[]){
    clients.forEach(element => {
        if((members.value.filter(el => el.id === element.clientId)).length === 0){
           members.value.push({name: element.username, id: element.clientId, color: "#fffff", progress: [] })
        }
        });
}


/*
 * Helper Functions 
*/

function handleAccessRequest(message : InviteMessage){
    newInvite.value.push(message)
}

function handleCollectionUpdate(message : UpdateCollectionMessage){
    
    message.collection.confirmedUsers.forEach(element => {
    if((members.value.filter(el => el.id === element.clientId)).length === 0){
       members.value.push({name: element.username, id: element.clientId, color: "#fffff", progress: [] })
    }
    });
}

function handleIncrementalTracks(message : IncrementalTrackMessage){

    let memberOfTrack = (members.value.filter(el => el.id === message.clientId))[0]

    let listOfTracks = memberOfTrack.progress.filter(el => el.id === message.trackId)

    if(listOfTracks.length == 0){
        memberOfTrack.progress.push({id: message.trackId, track: message.track})
    }else{
        listOfTracks[0].track.coordinates.push.apply(listOfTracks[0].track.coordinates, message.track.coordinates)
    }

    memberOfTrack.progress.push.apply(memberOfTrack.progress, message.track)
}