using FlatBackend.Models;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Core.Configuration;
using System.Text.Json;

namespace FlatBackend.Database
{
    public class MongoDBService
    {
        public MongoClient Mongo;
        public IMongoCollection<CollectionModel> collection;

        public MongoDBService()
        {
            Mongo = new MongoClient(Program.DBConnectionString);
            collection = Mongo.GetDatabase("CollectionsDatabase").GetCollection<CollectionModel>("collections");
        }

        public async Task AddCollection( CollectionModel col )
        {
            try
            {
                collection.InsertOneAsync(col);
                return;
            }
            catch (Exception ex) { Console.WriteLine(ex.ToString()); return; }
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
            try
            {
                return await collection.Find(r => r.id == id).FirstAsync();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex); return null;
            }
        }
    }
}