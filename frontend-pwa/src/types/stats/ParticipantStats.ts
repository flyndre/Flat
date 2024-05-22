import { Participant } from '@/types/Participant';

export type ParticipantStats = Participant & {
    /** The total distance covered by the participant, in meters. */
    coveredDistance: number;
};
