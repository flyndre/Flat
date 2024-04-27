using FlatBackend.Models;

namespace FlatBackend.DTOs
{
    public class CollectionUpdateDto 
    {
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.CollectionUpdate;
        public CollectionModel collection { get; set; }
    }
}