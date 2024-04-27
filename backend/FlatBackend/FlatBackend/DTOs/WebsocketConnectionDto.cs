namespace FlatBackend.DTOs
{
    public class WebsocketConnectionDto 
    {
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.WebsocketConnection;
        public Guid clientId { get; set; }
        public Guid collectionId { get; set; }
    }
}