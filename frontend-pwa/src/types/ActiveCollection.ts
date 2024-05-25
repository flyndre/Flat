import { ActiveDivision } from './ActiveDivision';
import { JoinRequest } from './JoinRequest';
import { ParticipantTrack } from './ParticipantTrack';

export type ActiveCollection = {
    id: string;
    name?: string;
    adminClientId: string;
    startDate?: Date;
    area?: GeoJSON.MultiPolygon;
    divisions?: ActiveDivision[];
    confirmedUsers: ParticipantTrack[];
    requestedUsers: JoinRequest[];
};
