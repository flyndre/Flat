using GeoJSON.Net;
using GeoJSON.Net.Feature;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Driver.GeoJsonObjectModel;
using ThirdParty.Json.LitJson;

namespace FlatBackend.Models
{
    public class CollectionModel
    {
        //[BsonId]
        //[BsonRepresentation(BsonType.ObjectId)]
        //public string? mongoId { get; set; }

        public Guid id { get; set; } = Guid.NewGuid();

        public Guid clientId { get; set; }
        public string? name { get; set; }

        public FeatureCollection area { get; set; }

        public List<AreaModel>? collectionArea { get; set; }
        public List<UserModel>? confirmedUsers { get; set; }
        public List<UserModel>? requestedAccess { get; set; }
    }
}