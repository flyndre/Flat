import { MultiPolygon } from "geojson";
import { StringMappingType } from "typescript";
import { ActiveDivision } from "../ActiveDivision";

export type UpdateCollectionMessage = {
    type: string;
    collection : {
        id: string;
        clientId: string;
        name: string;
        area: MultiPolygon;
        collectionDivision: ActiveDivision[];
        confirmedUsers: {
            clientId: string;
            username: string;
            accepted: boolean;
        }[]

    }
};