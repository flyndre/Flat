import { DivisionStats } from '../DivisionStats';
import { Participant } from '../Participant';
import { ParticipantStats } from '../ParticipantStats';

export type CollectionStats = {
    id: string;
    name: string;
    admin: Participant;
    /** The total area covered by the collection's divisions,  in square meters. */
    converedArea: number;
    divisionStats: DivisionStats[];
    participantStats: ParticipantStats[];
};
