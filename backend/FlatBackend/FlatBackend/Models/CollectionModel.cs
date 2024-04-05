using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace FlatBackend.Models
{
    public class CollectionModel
    {
        public ObjectId Id = ObjectId.GenerateNewId();
        public int OwnerId { get; set; }
        public string Name { get; set; }

        public List<AreaModel>? CollectionArea { get; set; }
        public List<UserModel>? ConfirmedUsers { get; set; }
        public List<UserModel>? RequestedAccess { get; set; }
    }
}