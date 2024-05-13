import { member } from "../types/member";
import { TrackingLog } from '@/types/TrackingLog';
import { useObservable } from '@vueuse/rxjs';
import { WebSocketStatus, useIntervalFn } from '@vueuse/core';
import { SERVER_UPDATE_INTERVAL } from '@/data/constants';
import { LineString } from 'geojson';
import { liveQuery } from 'dexie';
import db from '../data/db';
import { useWebSocket } from '@vueuse/core'
import { Ref, ref, watch } from "vue";
import { mdiConsoleLine } from "@mdi/js";
import { clientId } from "@/data/clientMetadata";


 
export let members = ref([] as member[]);
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
        var lineStringOfPosition = {type: "LineString", coordinates: []} as LineString
        
        var newlatest = null;
        db.trackingLogs.toCollection().each(el => {(el.timestamp > latestSendTimestamp) || latestSendTimestamp == null ? lineStringOfPosition.coordinates.push(el.position) : null; newlatest = el.timestamp})
    
        latestSendTimestamp = newlatest;

        send(JSON.stringify({type: 1, trackId: "94042b6e-a317-499a-af3d-1d32e58cbbb2", track: lineStringOfPosition, clientId: clientId.value}))
    },
    7000
);

watch(data, data => {
    console.log("New Websocket Message:")
    console.log(data)
    if(JSON.parse(data).type === "AccessRequest"){
        newInvite.value.push(JSON.parse(data))
    }
    if(JSON.parse(data).type === "CollectionUpdate"){
        console.log(JSON.parse(data).collection.confirmedUsers)
        JSON.parse(data).collection.confirmedUsers.forEach(element => {
            console.log(element); 
        if((members.value.filter(el => el.uuid === element.clientId)).length === 0){
            console.log("adding to Members:")
           members.value.push({name: element.username, uuid: element.clientId,  currentPosition: null , positionList: [] })
        }
        });
        console.log(members)
    }
    if(JSON.parse(data).type === "IncrementalTrack"){
        //let memberOfTrack = members.filter(el => {el.uuid === JSON.parse(data).clientId})[0]
        //memberOfTrack.positionList.push.apply(memberOfTrack.positionList, JSON.parse(data).track.coordinates)
    }
  
})

export function acceptOrDeclineAccessRequest(choice : boolean, username : string, clientId : string, collectionId : string){
    const answer = {
        type:  "AccessRequest",
        collectionId: collectionId,
        clientId: clientId,
        username: username,
        accepted: choice
    }

    console.log("ACCEPTORDECLINEMESSAGE:")
    console.log(answer)

    send(JSON.stringify(answer));
    console.log("Message send.") 
}

export function establishWebsocket(clientId : string, collectionId : string){


    send(JSON.stringify({
        type: 0, 
        clientId: clientId,
        collectionId: collectionId
    }))

    console.log("Established Websocket.")
    
    resumeInterval(); 
    
    
}

export function pauseSendingToServer(){
    pauseInterval();
}

export function resumeSendingToServer(){
    resumeInterval();
}

export function acceptInvite(uuid: string, username: string){
    members.push({name: username, uuid: uuid, currentPosition: undefined, positionList: []});
    console.log(members);
}



