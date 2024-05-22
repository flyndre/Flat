import {
    confirmRequest,
    divideCollectionArea,
    getCollection,
} from '@/api/rest';
import { clientId } from '@/data/clientMetadata';
import { SERVER_UPDATE_INTERVAL } from '@/data/constants';
import { trackingLogDB } from '@/data/trackingLogs';
import { ActiveCollection } from '@/types/ActiveCollection';
import { Division } from '@/types/Division';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { IncrementalTrackMessage } from '@/types/websocket/IncrementalTrackMessage';
import { InviteMessage } from '@/types/websocket/InviteMessage';
import { UpdateCollectionMessage } from '@/types/websocket/UpdateCollectionMessage';
import { getParticipantColor } from '@/util/trackingUtils';
import { useIntervalFn, useWebSocket } from '@vueuse/core';
import { computed, ref, watch } from 'vue';

let ws = null;


function initialiseWebsocket(){
    ws = new WebSocket('wss://flat.buhss.de/api/ws')
    ws.onmessage = function(event) {
    
        console.log("ON MESSAGE EVENT:")
        console.log(event)
        let websocketMsg = JSON.parse(event.data);
        Array.isArray(websocketMsg)
            ? websocketMsg.forEach((el) => handleWebsocketMessage(el))
            : handleWebsocketMessage(websocketMsg);
    }
    
    ws.onopen = function(event){
        console.log("ON OPEN EVENT:")
        console.log(event)
        establishWebsocket(clientId.value, _activeCollection.value.id)
    }
    
    ws.onclose = function(event){
        console.log("ON CLOSE EVENT:")
        console.log(event)
        ws = null
        initialiseWebsocket(); 
        //ws = new WebSocket('wss://flat.buhss.de/api/ws')
    }

    ws.onerror = function(event){
        console.log("HANDELSGUT WICHTIG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        console.log("ON ERROR EVENT:")
        console.log(event)
        console.log("HANDELSGUT WICHTIG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    }
}




const _isAdmin = ref(false);
const _activeCollection = ref<ActiveCollection>({} as ActiveCollection);
const _isLoading = ref(true);
let latestSendTimestamp = Date.now();

const {
    isActive,
    pause: pauseInterval,
    resume: resumeInterval,
} = useIntervalFn(
    async () => {
        console.log('Sending Trackingpoints...');

        let tracks = await trackingLogDB
            .where('timestamp')
            .above(latestSendTimestamp)
            .toArray();
        let result = Object.groupBy(tracks, ({ trackId }) => trackId);

        Object.entries(result).forEach(([key, logs]) => {
            var lineStringOfPosition = {
                type: 'LineString',
                coordinates: logs.map((el) => el.position),
            };

            const msg = {
                type: "IncrementalTrack",
                trackId: key,
                track: lineStringOfPosition,
                clientId: clientId.value,
            };
            console.log("Sending this Message:");
            console.log(msg);
            ws.send(JSON.stringify(msg));
        });


    console.log(tracks);
    latestSendTimestamp = tracks.at(-1)?.timestamp ?? Date.now();
}, SERVER_UPDATE_INTERVAL);


function handleWebsocketMessage(message: any) {
    switch (message.type) {
        case 'AccessRequest':
            handleAccessRequest(<InviteMessage>message);
            break;
        case 'CollectionUpdate':
            handleCollectionUpdate(<UpdateCollectionMessage>message);
            break;
        case 'IncrementalTrack':
            handleIncrementalTracks(<IncrementalTrackMessage>message);
            break;

        //LeaveMessage
        //DeleteMessage
    }
}

function _assignDivision(d: Division, p: ParticipantTrack | null) {
    let div = _activeCollection.value.divisions.find((el) => d.id === el.id);

    div.clientId = p === null ? null : p.id;
    divideCollectionArea(_activeCollection.value.id, [div]);

    if (p !== null) {
        let user = _activeCollection.value.confirmedUsers.filter(
            (el) => el.id === p.id
        )[0];
        user.color = div.color;
    }
}

function _startTracking() {
    resumeInterval();
}
function _stopTracking() {
    pauseInterval();
}

export function _closeCollection(collectionId: string) {
    const answer = { type: 'CollectionClosed', collectionId: collectionId };
    ws.send(JSON.stringify(answer));
}

export function _acceptOrDeclineAccessRequest(
    choice: boolean,
    username: string,
    clientId: string,
    collectionId: string
) {
    confirmRequest(username, clientId, choice, collectionId);
    _activeCollection.value.requestedUsers.shift();
}

export function establishWebsocket(clientId: string, collectionId: string) {
    
    ws.send(
        JSON.stringify({
            type: 'WebsocketConnection',
            clientId: clientId,
            collectionId: collectionId,
        })
    );
}

export const useCollectionService = (id: string) => {
    
    _activeCollection.value.id = id; 
    let response = getCollection(id, clientId.value);

    response.then(({ data }) => {
        console.log(data);
        _activeCollection.value.id = data.id;
        _activeCollection.value.adminClientId = data.clientId;
        _activeCollection.value.name = data.name;
        _activeCollection.value.area = data.area;
        _activeCollection.value.divisions = data.collectionDivision;
        _activeCollection.value.requestedUsers = [];
        _activeCollection.value.confirmedUsers = data.confirmedUsers.map(
            (user) => {
                return {
                    name: user.username,
                    id: user.clientId,
                    color: getParticipantColor(
                        user.clientId,
                        data.collectionDivision
                    ),
                    progress: [],
                };
            }
        );

        _activeCollection.value.requestedUsers = data.requestedUsers;
        _isAdmin.value = data.clientId === clientId.value;
        
        _isLoading.value = false;
    });

    initialiseWebsocket();
    
    console.log('READY');
    return {
        activeCollection: computed(() => ({
            ..._activeCollection.value,
            confirmedUsers: _activeCollection.value.confirmedUsers?.map(
                (u) => ({
                    ...u,
                    color: getParticipantColor(
                        u.id,
                        _activeCollection.value.divisions
                    ),
                })
            ),
        })),
        assignDivision: (d: Division, p: ParticipantTrack | null) =>
            _assignDivision(d, p),
        requests: computed(() => _activeCollection.value.requestedUsers),
        member: computed(() => _activeCollection.value.confirmedUsers),
        handleRequest: (
            choice: boolean,
            username: string,
            clientId: string,
            collectionId: string
        ) =>
            _acceptOrDeclineAccessRequest(
                choice,
                username,
                clientId,
                collectionId
            ),
        closeCollection: (collectionId: string) =>
            _closeCollection(collectionId),
        startTracking: _startTracking,
        stopTracking: _stopTracking,
        isLoading: computed(() => _isLoading.value),
        isAdmin: computed(() => _isAdmin.value),
        connectionStatus: status,
    };
};

/*
 * Helper Functions
 */

function handleAccessRequest(message: InviteMessage) {
    console.log('TEST:');
    console.log(_activeCollection.value.requestedUsers);
    _activeCollection.value.requestedUsers = [];
    _activeCollection.value.requestedUsers.push({
        username: message.username,
        clientId: message.clientId,
        accepted: null,
        collectionId: _activeCollection.value.id,
    });
}

function handleCollectionUpdate(message: UpdateCollectionMessage) {
    _activeCollection.value.divisions = message.collection.collectionDivision;
    _activeCollection.value.area = message.collection.area;
    _activeCollection.value.name = message.collection.name;

    message.collection.confirmedUsers.forEach((element) => {
        if (
            _activeCollection.value.confirmedUsers.filter(
                (el) => el.id === element.clientId
            ).length === 0
        ) {
            _activeCollection.value.confirmedUsers.push({
                name: element.username,
                id: element.clientId,
                color: getParticipantColor(
                    element.clientId,
                    message.collection.collectionDivision
                ),
                progress: [],
            });
        }
    });
}

function handleIncrementalTracks(message: IncrementalTrackMessage) {
    
    console.log(message)
    let memberOfTrack = _activeCollection.value.confirmedUsers.find(
        (el) => el.id === message.clientId
    );

    console.log("Owner of Track:")
    console.log(memberOfTrack); 

    let listOfTracks = memberOfTrack.progress.filter(
        (el) => el.id === message.trackId
    );

    if (listOfTracks.length == 0) {
        memberOfTrack.progress.push({
            id: message.trackId,
            track: message.track,
        });
    } else {
        listOfTracks[0].track.coordinates.push.apply(
            listOfTracks[0].track.coordinates,
            message.track.coordinates
        );
    }

    memberOfTrack.progress.push.apply(memberOfTrack.progress, message.track);
}