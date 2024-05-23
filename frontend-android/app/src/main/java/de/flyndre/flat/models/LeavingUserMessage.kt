package de.flyndre.flat.models

import kotlinx.serialization.Serializable

@Serializable
class LeavingUserMessage(
    var user:UserModel
): WebSocketMessage(WebSocketMessageType.LeavingUser){
}