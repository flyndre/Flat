import { FeatureCollection } from "geojson";

/**
 * Query for locations by search term.
 *
 * @param term any search string to find locations
 * @returns a list of matching results
 */
export async function query(term: string) {
    const request = `https://nominatim.openstreetmap.org/search?q=${term}&format=geojson`;
    const response = await fetch(request);
    const geojson = await response.json();
    return geojson as FeatureCollection;
}
