using FlatBackend.DTOs;

namespace FlatBackend.Models
{
    public class SummaryModel
    {
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.Summary;
        public CollectionModel collection { get; set; }
        public TrackCollectionModel trackCollection { get; set; }
    }
}