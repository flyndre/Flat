import { Overlay } from '@/types/map/Overlay';

export type TypedOverlay = {
    overlay: Overlay;
    type: google.maps.drawing.OverlayType;
};
