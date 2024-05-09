using FlatBackend.Models;

namespace FlatBackend.DTOs
{
    public class ResultAccessRequest
    {
        public bool accepted { get; set; }
        public CollectionModel collection { get; set; }
    }
}