using FlatBackend.DTOs;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace FlatBackend.Models
{
    public class SummaryModel
    {
        [JsonConverter(typeof(StringEnumConverter))]
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.Summary;

        public CollectionModel collection { get; set; }
        public TrackCollectionModel trackCollection { get; set; }
    }
}