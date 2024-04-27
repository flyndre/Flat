using FlatBackend.Models.GeoJsonModels;

namespace FlatBackend.DTOs
{
    public class TrackCollectionDto 
    {
        public Guid trackCollectionId { get; set; } = Guid.NewGuid();

        public Guid collectionId { get; set; }
        public List<LineString> tracks { get; set; }
    }
}