import { LineString } from "geojson";
import { TrackInstance } from "../TrackInstance";

export type IncrementalTrackMessage = {
    clientId: string;
    trackId: string;
    track: LineString
};