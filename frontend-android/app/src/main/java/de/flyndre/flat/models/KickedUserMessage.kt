package de.flyndre.flat.models

import kotlinx.serialization.Serializable

@Serializable
class KickedUserMessage(
    var message:String
):WebSocketMessage(WebSocketMessageType.KickedUser) {
}