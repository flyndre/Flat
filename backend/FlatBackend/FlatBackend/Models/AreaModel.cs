namespace FlatBackend.Models
{
    public class AreaModel
    {
        public Guid Id { get; set; }
        public int AssignedUserId { get; set; }
        public string GEOJson { get; set; }
    }
}