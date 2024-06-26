import { Participant } from './Participant';
import { TrackInstance } from './TrackInstance';

export type ParticipantTrack = Participant & {
    progress: TrackInstance[];
};
