import { Participant } from '../Participant';

export type ParticipantStats = Participant & {
    /** The total distance covered by the participant, in meters. */
    coveredDistance: number;
};
