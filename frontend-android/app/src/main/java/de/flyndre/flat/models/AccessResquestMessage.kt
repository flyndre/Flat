package de.flyndre.flat.models

import java.util.UUID

class AccessResquestMessage(
    collectionId:UUID,
    userId: UUID,
    username: String
) :WebSocketMessage(WebSocketMessageType.AccessRequest){

}
