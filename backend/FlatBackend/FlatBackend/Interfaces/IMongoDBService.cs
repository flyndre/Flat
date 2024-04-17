using FlatBackend.Models;

namespace FlatBackend.Interfaces
{
    public interface IMongoDBService
    {
        public Task AddCollection(CollectionModel col);

        public void ChangeCollection(CollectionModel col);

        public void RemoveCollection(Guid id);

        public Task<CollectionModel> GetCollection(Guid id);
    }
}
