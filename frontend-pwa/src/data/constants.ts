export const TOAST_LIFE = 300;
export const GOOGLE_MAPS_API_KEY = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
export const GOOGLE_MAPS_API_LIBRARIES: (
    | 'geometry'
    | 'drawing'
    | 'localContext'
    | 'places'
    | 'visualization'
)[] = ['drawing', 'places'];
