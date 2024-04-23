namespace FlatBackend.Models.GeoJsonModels
{
    public class LineString
    {
        public string type { get; set; } = "LineString";
        public List<List<float>> coordinates { get; set; }
    }
}