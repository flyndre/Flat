using FlatBackend.Models.GeoJsonModels;
using MongoDB.Driver.GeoJsonObjectModel;

namespace FlatBackend.Models
{
    public class AreaModel
    {
        public Guid id { get; set; }
        public Guid? clientId { get; set; }
        public string name { get; set; }
        public string color { get; set; }
        public PolygonModel area { get; set; }
    }
}