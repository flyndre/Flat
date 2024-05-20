import { Division } from '@/types/Division';
import { blendColors } from './colorUtils';

export function getParticipantColor(
    participantId: string,
    divisions: Division[]
) {
    const assignedDivisionColors = divisions
        .filter((d) => d.clientId === participantId)
        .map((d) => d.color);
    const blendAmount = 1 / assignedDivisionColors.length;
    const averageColor = assignedDivisionColors.reduce((previous, current) =>
        blendColors(previous, current, blendAmount)
    );
    return averageColor;
}
