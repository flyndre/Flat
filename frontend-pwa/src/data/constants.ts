export const TRACKING_INTERVAL = 1000;
export const SERVER_UPDATE_INTERVAL = 6000;
export const TOAST_LIFE = 3000;
export const GOOGLE_MAPS_API_KEY = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
export const UNASSIGNED_PARTICIPANT_COLOR = '#64748b';
export const INACTIVE_PARTICIPANT_COLOR = '#cdcdcd';
export const NUMBER_FORMAT_OPTIONS: Intl.NumberFormatOptions = {
    maximumFractionDigits: 2,
    minimumFractionDigits: 0,
};
export const GOOGLE_MAPS_API_LIBRARIES: (
    | 'geometry'
    | 'drawing'
    | 'localContext'
    | 'places'
    | 'visualization'
)[] = ['drawing', 'places'];
