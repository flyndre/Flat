using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using ThirdParty.Json.LitJson;

namespace FlatBackend.Models
{
    public class CollectionModel
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string? Id { get; set; }

        public int OwnerId { get; set; }
        public string? Name { get; set; }

        public List<AreaModel>? CollectionArea { get; set; }
        public List<UserModel>? ConfirmedUsers { get; set; }
        public List<UserModel>? RequestedAccess { get; set; }
    }
}