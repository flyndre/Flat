namespace FlatBackend.DTOs
{
    public enum WebSocketMessageType
    {
        WebsocketConnection,
        IncrementalTrack,
        AccessRequest,
        CollectionClosed,
        CollectionUpdate,
        Summary
    }
}