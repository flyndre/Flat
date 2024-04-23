using FlatBackend.Models;

namespace FlatBackend.DTOs
{
    public class CollectionUpdateDto
    {
        public WebSocketMessageType type = WebSocketMessageType.CollectionUpdate;
        public CollectionModel collection { get; set; }
    }
}