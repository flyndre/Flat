import { Division } from './Division';
import { JoinRequest } from './JoinRequest';
import { ParticipantTrack } from './ParticipantTrack';
import { ActiveDivision } from './ActiveDivision';

export type ActiveCollection = {
    id: string;
    name?: string;
    adminClientId: string;
    area?: GeoJSON.MultiPolygon;
    divisions?: ActiveDivision[];
    confirmedUsers: ParticipantTrack[];
    requestedUsers: JoinRequest[];

};
