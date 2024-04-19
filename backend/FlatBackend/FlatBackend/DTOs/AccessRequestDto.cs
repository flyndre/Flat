namespace FlatBackend.DTOs
{
    public class AccessRequestDto
    {
        public Guid clientId { get; set; }
        public string username { get; set; }
        public Guid collectionId { get; set; } //Added cause Boss can have multiple Collections...
    }
}