package de.flyndre.flat.models

import kotlinx.serialization.Serializable

@Serializable
class CollectionUpdateMessage (
    val collection: CollectionInstance
)
    : WebSocketMessage(WebSocketMessageType.CollectionUpdate)