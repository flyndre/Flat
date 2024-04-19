package de.flyndre.flat.models

class CollectionUpdateMessage (
    val collection: CollectionInstance
)
    : WebSocketMessage(WebSocketMessageType.CollectionUpdate)