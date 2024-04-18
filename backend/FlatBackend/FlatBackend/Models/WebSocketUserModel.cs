using FlatBackend.Controllers;
using FlatBackend.Websocket;
using System.Net.WebSockets;

namespace FlatBackend.Models
{
    public class WebSocketUserModel
    {
        public Guid clientId { get; set; }
        public Guid collectionId { get; set; }
        public WebSocket webSocket { get; set; }
    }
}