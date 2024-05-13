import { TrackInstance } from './TrackInstance';

export type ParticipantTrack = {
    id: string;
    name: string;
    color: string;
    progress: TrackInstance[];
};
