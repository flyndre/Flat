package de.flyndre.flat.models

import kotlinx.serialization.Serializable

@Serializable
class CollectionClosedMessage:WebSocketMessage(WebSocketMessageType.CollectionClosed) {
}