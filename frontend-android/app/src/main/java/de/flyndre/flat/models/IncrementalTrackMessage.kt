package de.flyndre.flat.models

import io.github.dellisd.spatialk.geojson.LineString
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class IncrementalTrackMessage(var trackId: String, var track: LineString):WebSocketMessage(WebSocketMessageType.IncrementalTrack){
}