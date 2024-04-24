using FlatBackend.Models.GeoJsonModels;

namespace FlatBackend.Models
{
    public class TrackCollectionModel
    {
        public Guid trackId { get; set; }
        public Guid clientId { get; set; }
        public Guid collectionId { get; set; }
        public List<LineString> tracks { get; set; }
    }
}