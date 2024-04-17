namespace FlatBackend.Models.GeoJsonModels
{
    public class PolygonModel
    {
        public string type { get; set; } = "Polygon";
        public List<List<List<float>>> coordinates { get; set; }
    }
}