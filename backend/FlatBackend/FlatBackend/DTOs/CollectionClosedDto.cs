using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace FlatBackend.DTOs
{
    public class CollectionClosedDto
    {
        [JsonConverter(typeof(StringEnumConverter))]
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.CollectionClosed;

        public Guid collectionId { get; set; }
    }
}