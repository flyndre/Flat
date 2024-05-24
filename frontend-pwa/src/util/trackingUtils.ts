import {
    INACTIVE_PARTICIPANT_COLOR,
    UNASSIGNED_PARTICIPANT_COLOR,
} from '@/data/constants';
import { Division } from '@/types/Division';
import { Participant } from '@/types/Participant';
import { blendColors } from './colorUtils';

export function getParticipantColor(
    participant: Participant,
    divisions: Division[]
) {
    if (participant == null || divisions == null || divisions.length === 0)
        return UNASSIGNED_PARTICIPANT_COLOR;
    if (participant.active === false) return INACTIVE_PARTICIPANT_COLOR;
    const assignedDivisionColors = getAssignedDivisions(
        participant,
        divisions
    ).map((d) => d.color);
    switch (assignedDivisionColors.length) {
        case 0:
            return UNASSIGNED_PARTICIPANT_COLOR;

        case 1:
            return assignedDivisionColors[0];

        default:
            const blendAmount = 1 / assignedDivisionColors.length;
            const averageColor = assignedDivisionColors.reduce(
                (previous, current) =>
                    blendColors(previous, current, blendAmount)
            );
            return averageColor;
    }
}

export function getAssignedDivisions(
    participant: Participant,
    divisions: Division[]
) {
    return divisions.filter((d) => d.clientId === participant.id);
}
