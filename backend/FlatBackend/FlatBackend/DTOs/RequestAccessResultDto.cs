using FlatBackend.Models;

namespace FlatBackend.DTOs
{
    public class RequestAccessResultDto
    {
        public bool accepted { get; set; }
        public CollectionModel? collection { get; set; }
    }
}