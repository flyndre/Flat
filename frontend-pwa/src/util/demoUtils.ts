import { InternalAxiosRequestConfig } from "axios";

function getData(adapterConfig: InternalAxiosRequestConfig) {
    const { baseURL: base, url: path, method } = adapterConfig;
    if (base === undefined || path === undefined) {
        return {};
    }
    let url: URL;
    try {
        url = new URL(path, base);
    } catch (error) {
        return {};
    }
    const operation = `${method?.toUpperCase()} ${url.pathname}`;
    switch (true) {
        case operation.startsWith('GET /api/rest/collection/'):
            return {
                id: 'abc',
                clientId: 'abc',
                name: 'sammli',
                area: [],
                collectionDivision: [],
                requestedUsers: [],
                confirmedUsers: []
            };
        case operation.startsWith('POST /api/rest/collection'):
            return {};
    }
    return {}
}

export const axiosDemoAdapter = async (adapterConfig: InternalAxiosRequestConfig) => ({
    status: 200,
    statusText: 'OK',
    data: getData(adapterConfig),
    headers: adapterConfig.headers,
    config: adapterConfig,
});
