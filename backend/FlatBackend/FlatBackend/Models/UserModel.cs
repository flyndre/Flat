namespace FlatBackend.Models
{
    public class UserModel
    {
        public Guid clientId { get; set; }
        public string username { get; set; }
        public bool accepted { get; set; } = false;
    }
}