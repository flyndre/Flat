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
import { ParticipantTrack } from "@/types/ParticipantTrack";


 
export let members = ref([{id: clientId.value, name: "admin", color: "#fffff", progress : []}] as ParticipantTrack[]);
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
        var lineStringOfPosition = {type: "LineString", coordinates: [[48.386850,8.580660], [48.387614, 8.581562], [48.387386, 8.577933]]} as LineString
        
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
        if((members.value.filter(el => el.id === element.clientId)).length === 0){
            console.log("adding to Members:")
           members.value.push({name: element.username, id: element.clientId, color: "#fffff", progress: [] })
        }
        });
        console.log(members)
    }
    if(JSON.parse(data).type === "IncrementalTrack"){
        console.log(members.value); 
        console.log(JSON.parse(data).clientId == members.value[0].id); 
        let memberOfTrack = (members.value.filter(el => el.id === JSON.parse(data).clientId))[0]

        let listOfTracks = memberOfTrack.progress.filter(el => el.id === JSON.parse(data).trackId)

        if(listOfTracks.length == 0){
            memberOfTrack.progress.push({id: JSON.parse(data).trackId, track: JSON.parse(data).track})
        }else{
            listOfTracks[0].track.coordinates.push.apply(listOfTracks[0].track.coordinates, JSON.parse(data).track.coordinates)
        }

        memberOfTrack.progress.push.apply(memberOfTrack.progress, JSON.parse(data).track)
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

    send(JSON.stringify(answer));

}

export function establishWebsocket(clientId : string, collectionId : string){
    const answer = {
        type: 0, 
        clientId: clientId,
        collectionId: collectionId
    }

    send(JSON.stringify(answer))

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
    console.log(members);
}



