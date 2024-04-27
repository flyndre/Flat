namespace FlatBackend.DTOs
{
    public class AccessConfirmationDto 
    {
        public Guid clientId { get; set; }
        public Guid collectionId { get; set; }
        public bool accepted { get; set; }
    }
}