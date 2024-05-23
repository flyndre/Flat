package de.flyndre.flat.models

enum class WebSocketMessageType {
    WebsocketConnection,
    IncrementalTrack,
    AccessRequest,
    CollectionClosed,
    CollectionUpdate,
    Summary,
    KeepAlive,
    LeavingUser,
    KickedUser

}