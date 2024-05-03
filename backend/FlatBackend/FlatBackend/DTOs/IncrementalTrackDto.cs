using FlatBackend.Models.GeoJsonModels;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace FlatBackend.DTOs
{
    public class IncrementalTrackDto
    {
        [JsonConverter(typeof(StringEnumConverter))]
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.IncrementalTrack;

        public string trackId { get; set; }
        public LineString track { get; set; }//LineString
    }
}