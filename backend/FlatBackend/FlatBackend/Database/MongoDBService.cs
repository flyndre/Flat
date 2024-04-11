using FlatBackend.Models;
using MongoDB.Bson;
using MongoDB.Driver;
using System.Text.Json;

namespace FlatBackend.Database
{
    public class MongoDBService
    {
        public MongoClient Mongo;
        public IMongoCollection<CollectionModel> collection;

        public MongoDBService()
        {
            Mongo = new MongoClient("mongodb://localhost:27017");
            collection = Mongo.GetDatabase("CollectionsDatabase").GetCollection<CollectionModel>("collections");
        }

        public async void AddCollection( CollectionModel col )
        {
            collection.InsertOneAsync(col);
            return;
        }

        public void ChangeCollection( CollectionModel col )
        {
            collection.FindOneAndReplace(replacement => replacement.id == col.id, col);
            return;
        }

        public void RemoveCollection( Guid id )
        {
            collection.DeleteOne(r => r.id == id);
            return;
        }

        public async Task<CollectionModel> GetCollection( Guid id )
        {
            return await collection.Find(r => r.id == id).FirstAsync();
        }
    }
}