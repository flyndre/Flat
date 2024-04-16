using GeoJSON.Net;
using GeoJSON.Net.Feature;

namespace FlatBackend.Models
{
    public class AreaModel
    {
        public Guid id { get; set; }
        public int clientId { get; set; }
        public FeatureCollection area { get; set; }
    }
}