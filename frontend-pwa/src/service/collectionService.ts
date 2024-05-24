import {
    confirmRequest,
    divideCollectionArea,
    getCollection,
    kickUser,
    leaveCollection,
} from '@/api/rest';
import { clientId } from '@/data/clientMetadata';
import { lastActiveCollection } from '@/data/collections';
import { SERVER_UPDATE_INTERVAL } from '@/data/constants';
import { trackingLogDB } from '@/data/trackingLogs';
import { ActiveCollection } from '@/types/ActiveCollection';
import { Division } from '@/types/Division';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { IncrementalTrackMessage } from '@/types/websocket/IncrementalTrackMessage';
import { InviteMessage } from '@/types/websocket/InviteMessage';
import { KickMessage } from '@/types/websocket/KickMessage';
import { UpdateCollectionMessage } from '@/types/websocket/UpdateCollectionMessage';
import { mergeActiveCollections } from '@/util/activeCollectionUtils';
import { getParticipantColor } from '@/util/trackingUtils';
import { useIntervalFn, watchDebounced } from '@vueuse/core';
import { computed, ref } from 'vue';

let ws: WebSocket = null;
const _websocketStatus = ref<number>(null);

function initialiseWebsocket() {
    ws = new WebSocket(import.meta.env.VITE_WS_BASE_URL);

    ws.onmessage = function (event) {
        _websocketStatus.value = ws.readyState;
        console.log('ON MESSAGE EVENT:');
        console.log(event);
        let websocketMsg = JSON.parse(event.data);
        Array.isArray(websocketMsg)
            ? websocketMsg.forEach((el) => handleWebsocketMessage(el))
            : handleWebsocketMessage(websocketMsg);
    };

    ws.onopen = function (event) {
        _websocketStatus.value = ws.readyState;
        console.log('ON OPEN EVENT:');
        console.log(event);
        establishWebsocket(clientId.value, _activeCollection.value.id);
    };

    ws.onclose = function (event) {
        _websocketStatus.value = ws.readyState;
        console.log('ON CLOSE EVENT:');
        console.log(event);
        ws = null;
        initialiseWebsocket();
    };

    ws.onerror = function (event) {
        _websocketStatus.value = ws.readyState;
        console.log(
            'HANDELSGUT WICHTIG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
        );
        console.log('ON ERROR EVENT:');
        console.log(event);
        console.log(
            'HANDELSGUT WICHTIG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
        );
    };
}

const _isAdmin = ref(false);
const _activeCollection = ref<ActiveCollection>({} as ActiveCollection);
const _isLoading = ref(true);
const _kickMessage = ref("");
let latestSendTimestamp = Date.now();

const {
    isActive: _isTracking,
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
                type: 'IncrementalTrack',
                trackId: key,
                track: lineStringOfPosition,
                clientId: clientId.value,
            };
            console.log('Sending this Message:');
            console.log(msg);
            ws.send(JSON.stringify(msg));
        });

        console.log(tracks);
        latestSendTimestamp = tracks.at(-1)?.timestamp ?? Date.now();
    },
    SERVER_UPDATE_INTERVAL,
    {
        immediate: false,
    }
);

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
        case 'KickedUser':
                handleKick(<KickMessage>message);
                break;

        // TODO:
        // LeaveMessage (message to admin that participant left)
        // DeleteMessage & EndCollectionMessage (messages to participants that collection is closed and message to admin with collection summary)
        // KickedUserMessage (message to participant that he has been kicked)
    }
}

function handleKick(message : KickMessage){
    _kickMessage.value = message.message;
}

function _assignDivision(d: Division, p: ParticipantTrack | null) {
    d.clientId = p === null ? null : p.id;
    divideCollectionArea(_activeCollection.value.id, [d]);
}

function _startTracking() {
    resumeInterval();
}
function _stopTracking() {
    pauseInterval();
}

export function _closeCollection(collectionId: string) {
    _stopTracking();
    const answer = { type: 'CollectionClosed', collectionId: collectionId };
    ws.send(JSON.stringify(answer));
    if (ws.readyState === ws.OPEN) ws.close();
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

let stopWatchHandle = null;

export const useCollectionService = (id: string) => {
    _activeCollection.value.id = id;
    let response = getCollection(id, clientId.value);

    response.then(({ data }) => {
        console.log(data);

        _isAdmin.value = data.clientId === clientId.value;

        const collection = {
            id: data.id,
            adminClientId: data.clientId,
            name: data.name,
            area: data.area,
            divisions: data.collectionDivision,
            requestedUsers: data.requestedUsers,
            confirmedUsers: data.confirmedUsers.map((user) => {
                return {
                    name: user.username,
                    id: user.clientId,
                    color: getParticipantColor(
                        user.clientId,
                        data.collectionDivision
                    ),
                    progress: [],
                };
            }),
        };

        const lastActive = lastActiveCollection.get();
        if (lastActive != null && lastActive.id === id) {
            _activeCollection.value = mergeActiveCollections(
                lastActive,
                collection
            );
        } else {
            lastActiveCollection.set(collection);
            _activeCollection.value = collection;
        }

        stopWatchHandle = watchDebounced(
            _activeCollection,
            (v) => lastActiveCollection.set(v),
            {
                deep: true,
                debounce: 1000,
            }
        );

        if (_isAdmin.value && _activeCollection.value.startDate == null) {
            _activeCollection.value.startDate = new Date();
        }

        _isLoading.value = false;
    });

    initialiseWebsocket();

    console.log('READY');
    return {
        activeCollection: computed(() => ({
            ..._activeCollection.value,
            confirmedUsers: _activeCollection.value.confirmedUsers?.map(
                (participant) => ({
                    ...participant,
                    color: getParticipantColor(
                        participant,
                        _activeCollection.value.divisions
                    ),
                })
            ),
        })),
        assignDivision: (d: Division, p: ParticipantTrack | null) =>
            _assignDivision(d, p),
        leave: async (collId: string, clientId: string) => await leaveCollection(collId, clientId),
        kick: async (collId: string, clId: string) => await kickUser(collId, clId, clientId.value),
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
        isTracking: _isTracking,
        isLoading: computed(() => _isLoading.value),
        isAdmin: computed(() => _isAdmin.value),
        connectionStatus: computed(() => _websocketStatus.value),
        kickMessage: computed(() => _kickMessage.value)
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
                progress: [],
            });
        }
    });

    _activeCollection.value.confirmedUsers =
        _activeCollection.value.confirmedUsers.map((existing) => ({
            ...existing,
            active:
                message.collection.confirmedUsers.find(
                    (active) => existing.id === active.clientId
                ) !== undefined,
        }));
}

function handleIncrementalTracks(message: IncrementalTrackMessage) {
    console.log(message);
    let memberOfTrack = _activeCollection.value.confirmedUsers.find(
        (el) => el.id === message.clientId
    );

    console.log('Owner of Track:');
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
