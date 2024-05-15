﻿using FlatBackend.Models;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace FlatBackend.DTOs
{
    public class LeavingUserDto
    {
        [JsonConverter(typeof(StringEnumConverter))]
        private WebSocketMessageType type { get; set; } = WebSocketMessageType.LeavingUser;

        public UserModel user { get; set; }
    }
}