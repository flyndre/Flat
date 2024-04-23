namespace FlatBackend.DTOs
{
    public class TrackCollectionDto
    {
        public Guid id { get; set; }
        public string trackCollection { get; set; } //MultiLineString
    }
}