import { UNASSIGNED_PARTICIPANT_COLOR } from '@/data/constants';
import { Division } from '@/types/Division';
import { blendColors } from './colorUtils';

export function getParticipantColor(
    participantId: string,
    divisions: Division[]
) {
    const assignedDivisionColors = divisions
        .filter((d) => d.clientId === participantId)
        .map((d) => d.color);
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
