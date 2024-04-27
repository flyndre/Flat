package de.flyndre.flat.models

import de.flyndre.flat.helper.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID
@Serializable
class WebsocketConnectionMessage(
    @Serializable(with=UUIDSerializer::class)
    var clientId: UUID,
    @Serializable(with=UUIDSerializer::class)
    var collectionId: UUID
): WebSocketMessage(type = WebSocketMessageType.WebsocketConnection) {
}