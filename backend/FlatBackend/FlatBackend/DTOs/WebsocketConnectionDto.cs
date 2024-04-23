namespace FlatBackend.DTOs
{
    public class WebsocketConnectionDto
    {
        public WebSocketMessageType type = WebSocketMessageType.WebsocketConnection;
        public Guid clientId { get; set; }
        public Guid collectionId { get; set; }
    }
}