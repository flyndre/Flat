import { MultiPolygon } from "geojson";
import { StringMappingType } from "typescript";

export type UpdateCollectionMessage = {
    type: string;
    collection : {
        id: string;
        clientId: string;
        name: string;
        area: MultiPolygon;
        confirmedUsers: {
            clientId: string;
            username: string;
            accepted: boolean;
        }[]
    }
};