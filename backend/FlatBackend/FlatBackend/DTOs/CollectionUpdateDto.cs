using FlatBackend.Models;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace FlatBackend.DTOs
{
    public class CollectionUpdateDto
    {
        [JsonConverter(typeof(StringEnumConverter))]
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.CollectionUpdate;

        public CollectionModel collection { get; set; }
    }
}