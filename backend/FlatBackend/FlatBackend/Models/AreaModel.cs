namespace FlatBackend.Models
{
    public class AreaModel
    {
        public int Id { get; set; }
        public int AssignedUserId { get; set; }
        public string GEOJson { get; set; }
    }
}