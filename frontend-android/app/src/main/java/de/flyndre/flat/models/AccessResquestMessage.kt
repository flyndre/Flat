package de.flyndre.flat.models

import java.util.UUID

class AccessResquestMessage(
    var collectionId:UUID,
    var userId: UUID,
    var username: String
) :WebSocketMessage(WebSocketMessageType.AccessRequest){

}
