using FlatBackend.Models.GeoJsonModels;

namespace FlatBackend.DTOs
{
    public class IncrementalTrackDto
    {
        public WebSocketMessageType type { get; set; } = WebSocketMessageType.IncrementalTrack;
        public Guid trackId { get; set; }
        public LineString track { get; set; }//LineString
    }
}