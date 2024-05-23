import { Division } from '@/types/Division';

export type DivisionStats = Division & {
    /** The area covered by the division, in square kilometers. */
    coveredArea: number;
};
