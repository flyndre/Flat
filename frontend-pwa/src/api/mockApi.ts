import { collectionDB } from '@/data/collections';
import MockAdapter from 'axios-mock-adapter';
import api from './api';

const mockApi = new MockAdapter(api, {
    delayResponse: 2000,
});

mockApi.onPost('api/rest/collection').reply((config) => {
    try {
        config.data = JSON.parse(config.data);
    } catch (e) {
        return [400, e];
    }
    if (config.data == null) {
        return [400];
    }
    return [200, {}];
});

const collectionGetRegex =
    /api\/rest\/collection\/(?<collectionId>.+)\?userId=(?<userId>.+)/;

mockApi.onGet(collectionGetRegex).reply(async (config) => {
    const matches = config.url.match(collectionGetRegex);
    if (matches == null || matches.groups == null) {
        return [404];
    }
    if (matches.groups.collectionId == null || matches.groups.userId == null) {
        return [400];
    }
    let collection = {
        id: matches.groups.collectionId,
        clientId: matches.groups.userId,
        name: 'Sammlung',
        area: null,
        collectionDivision: [],
        confirmedUsers: [
            {
                clientId: matches.groups.userId,
                username: 'admin',
                accepted: true,
            },
        ],
        requestedAccess: [],
    };
    try {
        const storedCollection = await collectionDB.get(
            matches.groups.collectionId
        );
        collection = {
            id: storedCollection.id,
            clientId: storedCollection.adminClientId,
            name: storedCollection.name,
            area: storedCollection.area,
            collectionDivision: storedCollection.divisions,
            confirmedUsers: [
                {
                    clientId: storedCollection.adminClientId,
                    username: 'admin',
                    accepted: true,
                },
            ],
            requestedAccess: [],
        };
    } catch (e) {}
    return [200, collection];
});

const collectionPutRegex = /api\/rest\/collection\/(?<collectionId>.+)/;

mockApi.onPut(collectionPutRegex).reply((config) => {
    const matches = config.url.match(collectionPutRegex);
    if (matches == null || matches.groups == null) {
        return [404];
    }
    try {
        config.data = JSON.parse(config.data);
    } catch (e) {
        return [400, e];
    }
    if (matches.groups.collectionId == null || !Array.isArray(config.data)) {
        return [400];
    }
    return [200, {}];
});

const collectionDeleteRegex = /api\/rest\/collection\/(?<collectionId>.+)/;

mockApi.onDelete(collectionDeleteRegex).reply((config) => {
    const matches = config.url.match(collectionDeleteRegex);
    if (matches == null || matches.groups == null) {
        return [404];
    }
    if (matches.groups.collectionId == null) {
        return [400];
    }
    return [200, {}];
});

const accessRequestPostRegex = /api\/rest\/accessrequest\/(?<collectionId>.+)/;

mockApi.onPost(accessRequestPostRegex).reply((config) => {
    const matches = config.url.match(accessRequestPostRegex);
    if (matches == null || matches.groups == null) {
        return [404];
    }
    try {
        config.data = JSON.parse(config.data);
    } catch (e) {
        return [400, e];
    }
    if (
        matches.groups.collectionId == null ||
        config.data?.username == null ||
        config.data?.clientId == null
    ) {
        return [400];
    }
    return [
        200,
        {
            accepted: true,
        },
    ];
});

const accessConfirmationRegex =
    /api\/rest\/AccessConfirmation\/(?<collectionId>.+)/;

mockApi.onPost(accessConfirmationRegex).reply((config) => {
    const matches = config.url.match(accessConfirmationRegex);
    if (matches == null || matches.groups == null) {
        return [404];
    }
    try {
        config.data = JSON.parse(config.data);
    } catch (e) {
        return [400, e];
    }
    if (
        matches.groups.collectionId == null ||
        config.data?.username == null ||
        config.data?.clientId == null ||
        config.data?.accepted == null
    ) {
        return [400];
    }
    return [200, {}];
});

const leaveCollectionRegex =
    /api\/rest\/LeaveCollection\/(?<collectionId>.+)\?clientId=(?<clientId>.+)/;

mockApi.onPost(leaveCollectionRegex).reply((config) => {
    const matches = config.url.match(leaveCollectionRegex);
    if (matches == null || matches.groups == null) {
        return [404];
    }
    if (
        matches.groups.collectionId == null ||
        matches.groups.clientId == null
    ) {
        return [400];
    }
    return [200, {}];
});

const removeUserRegex =
    /api\/rest\/RemoveUser\/(?<collectionId>.+)\?clientId=(?<clientId>.+)&bossId=(?<bossId>.+)/;

mockApi.onPost(removeUserRegex).reply((config) => {
    const matches = config.url.match(removeUserRegex);
    if (matches == null || matches.groups == null) {
        return [404];
    }
    if (
        matches.groups.collectionId == null ||
        matches.groups.clientId == null ||
        matches.groups.bossId == null
    ) {
        return [400];
    }
    return [200, {}];
});

export default api;
