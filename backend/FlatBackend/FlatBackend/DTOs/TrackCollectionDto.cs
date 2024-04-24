﻿using FlatBackend.Models.GeoJsonModels;

namespace FlatBackend.DTOs
{
    public class TrackCollectionDto
    {
        public Guid trackId { get; set; }
        public Guid clientId { get; set; }
        public Guid collectionId { get; set; }
        public List<LineString> tracks { get; set; }
    }
}