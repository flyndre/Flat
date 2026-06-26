import { MapType } from "@/types/map/MapType";
import { ColorSchemeType } from "@vueuse/core";
import { StyleSpecification } from "maplibre-gl";
import bright from '@/assets/maps/bright.json'
import fiord from '@/assets/maps/fijord.json'
import satellite from '@/assets/maps/satellite.json';
import terrain from '@/assets/maps/satellite-terrain.json';

export function getStyle(type: MapType, theme: ColorSchemeType = 'no-preference'): StyleSpecification {
    switch (type) {
        case 'roadmap':
            if (theme === 'dark') {
                return fiord as StyleSpecification
            } else {
                return bright as StyleSpecification
            }
        
        case 'satellite':
            return satellite as StyleSpecification
        
        case 'terrain':
            return terrain as StyleSpecification
        
        default:
            return fiord as StyleSpecification
    }
}
