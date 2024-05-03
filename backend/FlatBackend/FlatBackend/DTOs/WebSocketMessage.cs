using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace FlatBackend.DTOs
{
    public class WebSocketMessage
    {
        [JsonConverter(typeof(StringEnumConverter))]
        public WebSocketMessageType type { get; set; }
    }
}