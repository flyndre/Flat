namespace FlatBackend.Models.GeoJsonModels
{
    public class MultiPolygonModel
    {
        public string type { get; set; } = "MultiPolygon";
        public List<List<List<List<float>>>> coordinates { get; set; }
    }
}