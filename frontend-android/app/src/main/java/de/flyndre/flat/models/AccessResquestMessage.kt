package de.flyndre.flat.models

import de.flyndre.flat.helper.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class AccessResquestMessage(
    @Serializable(with = UUIDSerializer::class)
    var collectionId:UUID,
    @Serializable(with = UUIDSerializer::class)
    var clientId: UUID,
    var username: String
) :WebSocketMessage(WebSocketMessageType.AccessRequest){

}
