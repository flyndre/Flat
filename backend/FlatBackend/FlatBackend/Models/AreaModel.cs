﻿using FlatBackend.Models.GeoJsonModels;
using MongoDB.Driver.GeoJsonObjectModel;

namespace FlatBackend.Models
{
    public class AreaModel
    {
        public Guid id { get; set; }
        public int clientId { get; set; }
        public string name { get; set; }
        public PolygonModel area { get; set; }
    }
}