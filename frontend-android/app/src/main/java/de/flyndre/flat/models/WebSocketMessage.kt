package de.flyndre.flat.models

import kotlinx.serialization.Serializable

@Serializable
open class WebSocketMessage(var type: WebSocketMessageType) {

}