import { mdiCircle } from '@mdi/js';

export const POSITION_ICON_OUTER = {
    path: mdiCircle,
    fillColor: '#ec4899',
    fillOpacity: 0.25,
    strokeWeight: 0,
    scale: 1.15,
    anchor: new google.maps.Point(12, 12),
};

export const POSITION_ICON_INNER = {
    path: mdiCircle,
    fillColor: '#ec4899',
    fillOpacity: 1,
    strokeColor: 'white',
    strokeWeight: 1.7,
    scale: 0.62,
    anchor: new google.maps.Point(12, 12),
};
