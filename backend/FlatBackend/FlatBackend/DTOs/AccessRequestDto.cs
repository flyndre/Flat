namespace FlatBackend.DTOs
{
    public class AccessRequestDto
    {
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.AccessRequest;
        public Guid collectionId { get; set; }
        public Guid clientId { get; set; }
        public string username { get; set; }
        //Added cause Boss can have multiple Collections...
    }
}