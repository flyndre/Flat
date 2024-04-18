namespace FlatBackend.DTOs
{
    public class GPSTrackDto
    {
        public Guid trackId { get; set; }
        public string track { get; set; }//LineString
    }
}