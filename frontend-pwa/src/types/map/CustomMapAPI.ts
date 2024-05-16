import { Ref } from 'vue';
import { Division } from '../Division';

export type CustomMapApi = {
    deleteAllDivisions: () => void;
    deleteDivision: (division: Division) => void;
    deleteSelectedDivision: () => void;
    divisionSelected: Ref<boolean>;
    focusDivision: (division: Division) => void;
    panMapToDivision: (division: Division) => void;
    panMapToDivisions: (divisions: Division[]) => void;
    panMapToPos: (
        position: google.maps.LatLng | google.maps.LatLngLiteral
    ) => void;
    placesTextSearch: google.maps.places.PlacesService['textSearch'];
    setDivisionName: (division: Division, name: string) => void;
};
