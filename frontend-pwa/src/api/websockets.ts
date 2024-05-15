import { clientId } from '@/data/clientMetadata';
import { ParticipantTrack } from '@/types/ParticipantTrack';
import { useIntervalFn, useWebSocket } from '@vueuse/core';
import { LineString } from 'geojson';
import { ref, watch } from 'vue';
import db from '../data/db';

export let members = ref([
    { id: clientId.value, name: 'admin', color: '#fffff', progress: [] },
] as ParticipantTrack[]);
export let newInvite = ref([]);
export let isAdmin = true;
let latestSendTimestamp = null;

export function setAdmin(boo) {
    isAdmin = boo;
}

const { status, data, send, open, close } = useWebSocket(
    'wss://flat.buhss.de/api/ws',
    {
        autoReconnect: true,
    }
);
const {
    isActive,
    pause: pauseInterval,
    resume: resumeInterval,
} = useIntervalFn(() => {
    console.log('Sending Trackingpoints...');
    var lineStringOfPosition = {
        type: 'LineString',
        coordinates: [
            [48.38685, 8.58066],
            [48.387614, 8.581562],
            [48.387386, 8.577933],
        ],
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
    let websocketMessage = JSON.parse(data);
    switch (websocketMessage.type) {
        case 'AccessRequest':
            if (websocketMessage.type === 'AccessRequest') {
                newInvite.value.push(JSON.parse(data));
            }
            break;
        case 'CollectionUpdate':
            websocketMessage.collection.confirmedUsers.forEach((element) => {
                if (
                    members.value.filter((el) => el.id === element.clientId)
                        .length === 0
                ) {
                    members.value.push({
                        name: element.username,
                        id: element.clientId,
                        color: '#fffff',
                        progress: [],
                    });
                }
            });
            break;
        case 'IncrementalTrack':
            let memberOfTrack = members.value.filter(
                (el) => el.id === websocketMessage.clientId
            )[0];
            let listOfTracks = memberOfTrack.progress.filter(
                (el) => el.id === websocketMessage.trackId
            );

            if (listOfTracks.length == 0) {
                memberOfTrack.progress.push({
                    id: websocketMessage.trackId,
                    track: websocketMessage.track,
                });
            } else {
                listOfTracks[0].track.coordinates.push.apply(
                    listOfTracks[0].track.coordinates,
                    websocketMessage.track.coordinates
                );
            }

            memberOfTrack.progress.push.apply(
                memberOfTrack.progress,
                websocketMessage.track
            );
            break;
    }
});

export function closeCollection(collectionId: string) {
    const answer = { type: 3, collectionId: collectionId };
    send(JSON.stringify(answer));
}

export function acceptOrDeclineAccessRequest(
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

    send(JSON.stringify(answer));
}

export function establishWebsocket(clientId: string, collectionId: string) {
    const answer = {
        type: 0,
        clientId: clientId,
        collectionId: collectionId,
    };

    send(JSON.stringify(answer));

    console.log('Established Websocket.');

    resumeInterval();
}

export function pauseSendingToServer() {
    pauseInterval();
}

export function resumeSendingToServer() {
    resumeInterval();
}

export function acceptInvite(uuid: string, username: string) {
    console.log(members);
}
