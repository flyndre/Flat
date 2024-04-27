namespace FlatBackend.DTOs
{
    public class CollectionClosedDto 
    {
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.CollectionClosed;
        public Guid collectionId { get; set; }
    }
}