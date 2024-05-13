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


 
export let members = [] as member[];
export let newInvite = ref(undefined);
export let isAdmin = false;
let latestSendTimestamp = null;

export function setAdmin(boo){
    isAdmin = boo
}

const { status, data, send, open, close } = useWebSocket('wss://flat.buhss.de/api/ws')
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

        send(JSON.stringify({type: 1, trackId: "123", track: lineStringOfPosition}))
    },
    7000,
    {
        immediate: false,
    }
);

watch(data, data => {
    console.log("New Websocket Message:")
    console.log(data)
  
})

export function establishWebsocket(clientId : string, collectionId : string){


    send(JSON.stringify({
        type: 0, 
        clientId: clientId,
        collectionId: collectionId
    }))

    console.log("Esstablished Websocket.")
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



