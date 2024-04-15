using FlatBackend.Interfaces;
using FlatBackend.Models;
using MongoDB.Bson;
using MongoDB.Driver;
using System.Text.Json;

namespace FlatBackend.Database
{
    public class MongoDBService : IMongoDBService
    {
        public MongoClient Mongo;
        public IMongoCollection<CollectionModel> collection;

        public MongoDBService(string connectionString)
        {
            Mongo = new MongoClient(connectionString);
            collection = Mongo.GetDatabase("CollectionsDatabase").GetCollection<CollectionModel>("collections");
        }

        public async Task AddCollection( CollectionModel col )
        {
            await collection.InsertOneAsync(col);
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