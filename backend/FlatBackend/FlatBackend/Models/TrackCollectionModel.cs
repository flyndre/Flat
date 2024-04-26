using FlatBackend.DTOs;
using FlatBackend.Models.GeoJsonModels;

namespace FlatBackend.Models
{
    public class TrackCollectionModel
    {
        public Guid trackId { get; set; } = Guid.NewGuid();

        public Guid collectionId { get; set; }
        public List<IncrementalTrackDto> tracks { get; set; }
    }
}