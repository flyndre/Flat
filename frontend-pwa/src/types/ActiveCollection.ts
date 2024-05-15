import { Division } from './Division';
import { ParticipantTrack } from './ParticipantTrack';

export type ActiveCollection = {
    id: string;
    name?: string;
    adminClientId: string;
    area?: GeoJSON.MultiPolygon;
    divisions?: Division[];
    confirmedUsers: ParticipantTrack[];
    requestedUsers: ParticipantTrack[];

};
