import { Map, StyleSpecification } from "maplibre-gl";

/**
 * Sets the given `style` to the `map` using `setStyle` but while preserving any
 * shapes drawn on the map using terradraw.
 * 
 * @param map a `Map` instance 
 * @param style a `StyleSpecification` to assign
 */
export function setStyleSafe(map: Map, style: StyleSpecification) {
    map.setStyle(style, {
        transformStyle: (previousStyle, nextStyle) => {
            if (previousStyle === undefined) {
                return nextStyle;
            }
            
            const tdLayers = previousStyle.layers.filter(layer => layer.id.startsWith('td-'));
            const tdSources = Object.fromEntries(Object.entries(previousStyle.sources).filter(([id]) => id.startsWith('td-')));

            // Kopiere sie in den neuen Stil hinein
            return {
                ...nextStyle,
                sources: { ...nextStyle.sources, ...tdSources },
                layers: [...nextStyle.layers, ...tdLayers]
            };
        }
    });
}
