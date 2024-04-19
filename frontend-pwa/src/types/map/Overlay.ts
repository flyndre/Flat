export type Overlay = { type?: google.maps.drawing.OverlayType } & (
    | google.maps.Polygon
    | google.maps.Polyline
    | google.maps.Rectangle
    | google.maps.Circle
);
