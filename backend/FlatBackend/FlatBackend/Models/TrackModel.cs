using FlatBackend.Models.GeoJsonModels;

namespace FlatBackend.Models
{
    public class TrackModel
    {
        public Guid trackId { get; set; }
        public Guid? clientId { get; set; }
        public LineString incrementalTrack { get; set; }
    }
}