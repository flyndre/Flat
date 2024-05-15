import { getCollection } from '@/api/rest';
import { members, newInvite } from '@/api/websockets';
import { clientId } from '@/data/clientMetadata';
import db from '@/data/db';
import { Division } from '@/types/Division';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { ActiveCollection } from '@/types/activeCollection';
import { IncrementalTrackMessage } from '@/types/websocket/IncrementalTrackMessage';
import { InviteMessage } from '@/types/websocket/InviteMessage';
import { UpdateCollectionMessage } from '@/types/websocket/UpdateCollectionMessage';
import { useIntervalFn, useWebSocket } from '@vueuse/core';
import { LineString } from 'geojson';
import { computed, ref, watch } from 'vue';

const { status, data, send, open, close } = useWebSocket(
    'wss://flat.buhss.de/api/ws',
    {
        autoReconnect: true,
    }
);

const _activeCollection = ref({} as ActiveCollection);
let isAdmin = true;
let latestSendTimestamp = null;


const {
    isActive,
    pause: pauseInterval,
    resume: resumeInterval,
} = useIntervalFn(() => {
    console.log('Sending Trackingpoints...');
    var lineStringOfPosition = {
        type: 'LineString',
        coordinates: [[0, 0]],
    } as LineString;

    var newlatest = null;
    db.trackingLogs.toCollection().each((el) => {
        el.timestamp > latestSendTimestamp || latestSendTimestamp == null
            ? lineStringOfPosition.coordinates.push(el.position)
            : null;
        newlatest = el.timestamp;
    });

    latestSendTimestamp = newlatest;

    send(
        JSON.stringify({
            type: 1,
            trackId: '94042b6e-a317-499a-af3d-1d32e58cbbb2',
            track: lineStringOfPosition,
            clientId: clientId.value,
        })
    );
    }, 7000);

watch(data, (data) => {
    let websocketMsg = JSON.parse(data);
    switch (websocketMsg.type) {
        case 'AccessRequest':
            handleAccessRequest(websocketMsg as InviteMessage);
            break;
        case 'CollectionUpdate':
            handleCollectionUpdate(websocketMsg as UpdateCollectionMessage);
            break;
        case 'IncrementalTrack':
            handleIncrementalTracks(websocketMsg as IncrementalTrackMessage);
            break;
    }
});

function _assignDivision(d: Division, p: ParticipantTrack | null) {}
function _startTracking() {}
function _stopTracking() {}

export function _closeCollection(collectionId: string) {
    const answer = { type: 3, collectionId: collectionId };
    send(JSON.stringify(answer));
}

export function _acceptOrDeclineAccessRequest(
    choice: boolean,
    username: string,
    clientId: string,
    collectionId: string
) {
    const answer = {
        type: 'AccessRequest',
        collectionId: collectionId,
        clientId: clientId,
        username: username,
        accepted: choice,
    };
    console.log('Sending AcceptOrDeclineRequest:');
    console.log(answer);

    send(JSON.stringify(answer));
    newInvite.value.shift();
}

export function establishWebsocket(clientId: string, collectionId: string) {
    send(
        JSON.stringify({
            type: 'WebsocketConnection',
            clientId: clientId,
            collectionId: collectionId,
        })
    );
    resumeInterval();
}

export const useTrackingService = async (id: string) => {
    var response = await getCollection(
        id,
        clientId.value
    );
    _activeCollection.value = response.data;
    
    return {
        activeCollection: computed(() => _activeCollection.value),
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
    };
};

/*
 * Helper Functions
 */

function handleAccessRequest(message: InviteMessage) {
    _activeCollection.value.requestedUsers.push({name: message.username, id: message.clientId, progress: [], color: null});
}

function handleCollectionUpdate(message: UpdateCollectionMessage) {
    message.collection.confirmedUsers.forEach((element) => {
        if (
            _activeCollection.value.confirmedUsers.filter((el) => el.id === element.clientId).length ===
            0
        ) {
            _activeCollection.value.confirmedUsers.push({
                name: element.username,
                id: element.clientId,
                color: '#fffff',
                progress: [],
            });
        }
    });
}

function handleIncrementalTracks(message: IncrementalTrackMessage) {
    let memberOfTrack = _activeCollection.value.confirmedUsers.filter(
        (el) => el.id === message.clientId
    )[0];

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
