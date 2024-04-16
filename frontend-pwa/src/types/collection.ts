import { Division } from './Division';

export type Collection = {
    id: string;
    name?: string;
    adminClientId: string;
    area?: GeoJSON.Polygon;
    divisions?: Division[];
};
