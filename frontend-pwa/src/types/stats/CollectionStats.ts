import { Participant } from '@/types/Participant';
import { DivisionStats } from '@/types/stats/DivisionStats';
import { ParticipantStats } from '@/types/stats/ParticipantStats';

export type CollectionStats = {
    id: string;
    collectionId: string;
    name: string;
    admin: Participant;
    startDate?: Date;
    finishDate?: Date;
    /** The total area covered by the collection's divisions,  in square meters. */
    converedArea: number;
    divisionStats: DivisionStats[];
    participantStats: ParticipantStats[];
};
