package de.flyndre.flat.models

import de.flyndre.flat.helper.UUIDSerializer
import io.github.dellisd.spatialk.geojson.LineString
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class IncrementalTrackMessage(
    @Serializable(with = UUIDSerializer::class)
    var trackId: UUID,
    @Serializable(with= UUIDSerializer::class)
    var clientId:UUID,
    var track: LineString):WebSocketMessage(WebSocketMessageType.IncrementalTrack){
}