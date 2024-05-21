import { confirmRequest, divideCollectionArea, getCollection } from '@/api/rest';
import { isAdmin } from '@/api/websockets';
import { clientId } from '@/data/clientMetadata';
import { trackingLogDB } from '@/data/trackingLogs';
import { ActiveCollection } from '@/types/ActiveCollection';
import { Division } from '@/types/Division';
import { JoinRequest } from '@/types/JoinRequest';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { IncrementalTrackMessage } from '@/types/websocket/IncrementalTrackMessage';
import { InviteMessage } from '@/types/websocket/InviteMessage';
import { UpdateCollectionMessage } from '@/types/websocket/UpdateCollectionMessage';
import { getParticipantColor } from '@/util/trackingUtils';
import { useIntervalFn, useWebSocket } from '@vueuse/core';
import { LineString } from 'geojson';
import { computed, ref, watch } from 'vue';

const { status, data, send, open, close } = useWebSocket(
    'wss://flat.buhss.de/api/ws',
    {
        autoReconnect: true,
    }
);
const _isAdmin = ref(false);
const _activeCollection = ref({} as ActiveCollection);
const _isLoading = ref(true);

let latestSendTimestamp = null;
const dmettstett_DEBUG = [48.386848, 8.58066];

function randomDEBUG() {
    return [
        dmettstett_DEBUG[0] + Math.random() / 100,
        dmettstett_DEBUG[1] + Math.random() / 100,
    ];
}

const {
    isActive,
    pause: pauseInterval,
    resume: resumeInterval,
} = useIntervalFn(() => {
    console.log('Sending Trackingpoints...');
    var lineStringOfPosition = {
        type: 'LineString',
        coordinates: [randomDEBUG(), randomDEBUG(), randomDEBUG()],
    } as LineString;

    var newlatest = null;
    trackingLogDB.each((el) => {
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
    send(JSON.stringify(answer));
}

export function _acceptOrDeclineAccessRequest(
    choice: boolean,
    username: string,
    clientId: string,
    collectionId: string
) {

    confirmRequest(username, clientId, choice, collectionId)
    _activeCollection.value.requestedUsers.shift();
}

export function establishWebsocket(clientId: string, collectionId: string) {
    send(
        JSON.stringify({
            type: 'WebsocketConnection',
            clientId: clientId,
            collectionId: collectionId,
        })
    );
}

export const useCollectionService = (id: string) => {
    let response = getCollection(id, clientId.value);
    response.then(({ data }) => {
        _activeCollection.value.id = data.id;
        _activeCollection.value.adminClientId = data.clientId;
        _activeCollection.value.name = data.name;
        _activeCollection.value.area = data.area;
        _activeCollection.value.divisions = data.collectionDivision;
        _activeCollection.value.requestedUsers = [] as JoinRequest[];
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
        _activeCollection.value.confirmedUsers.push({
            name: 'manamana',
            id: '39c2beaf-bb10-4aad-99da-f3288aaaaaae',
            color: '#eff542',
            progress: [
                {
                    id: '39c2beaf-bb10-aaad-99da-f3288aaaaaae',
                    track: {
                        type: 'LineString',
                        coordinates: [
                            [48.38685, 8.58066],
                            [48.386916, 8.577717],
                            [48.388811, 8.583342],
                        ],
                    },
                },
            ],
        });
        _activeCollection.value.requestedUsers = data.requestedUsers;
        _isAdmin.value = data.clientId === clientId;
        _isLoading.value = false;
    });

    establishWebsocket(clientId.value, id);

    console.log('READY');
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
        isLoading: computed(() => _isLoading.value),
        isAdmin: computed(() => _isAdmin.value),
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
    message.collection.confirmedUsers.forEach((element) => {
        if (
            _activeCollection.value.confirmedUsers.filter(
                (el) => el.id === element.clientId
            ).length === 0
        ) {
            _activeCollection.value.confirmedUsers.push({
                name: element.username,
                id: element.clientId,
                color: '#eff542',
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
