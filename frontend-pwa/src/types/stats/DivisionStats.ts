import { Division } from '../Division';

export type DivisionStats = Division & {
    /** The area covered by the division, in square meters. */
    coveredArea: number;
};
