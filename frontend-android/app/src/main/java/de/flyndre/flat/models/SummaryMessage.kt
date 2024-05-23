package de.flyndre.flat.models

class SummaryMessage(
    var collection: CollectionInstance
):WebSocketMessage(WebSocketMessageType.Summary) {
}