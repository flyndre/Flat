namespace FlatBackend.Models
{
    public class CollectionModel
    {
        public int Id { get; set; }
        public int OwnerId { get; set; }
        public string Name { get; set; }
        public List<AreaModel>? CollectionArea { get; set; }
        public List<UserModel>? ConfirmedUsers { get; set; }
        public List<UserModel>? RequestedAccess { get; set; }
    }
}