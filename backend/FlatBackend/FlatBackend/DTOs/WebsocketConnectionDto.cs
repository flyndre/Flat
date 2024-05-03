using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace FlatBackend.DTOs
{
    public class WebsocketConnectionDto
    {
        [JsonConverter(typeof(StringEnumConverter))]
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.WebsocketConnection;

        public Guid clientId { get; set; }
        public Guid collectionId { get; set; }
    }
}