import { TypedOverlay } from '@/types/map/TypedOverlay';

export type IdentifyableTypedOverlay = TypedOverlay & {
    id: string;
    name?: string;
};
