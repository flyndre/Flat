import { Participant } from '@/types/Participant';
import { DivisionStats } from '@/types/stats/DivisionStats';
import { ParticipantStats } from '@/types/stats/ParticipantStats';

export type CollectionStats = {
    id: string;
    name: string;
    admin: Participant;
    /** The total area covered by the collection's divisions,  in square meters. */
    converedArea: number;
    divisionStats: DivisionStats[];
    participantStats: ParticipantStats[];
};
