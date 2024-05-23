import { ParticipantTrack } from '@/types/ParticipantTrack';

export type ParticipantStats = ParticipantTrack & {
    /** The total distance covered by the participant, in kilometers. */
    coveredDistance: number;
};
