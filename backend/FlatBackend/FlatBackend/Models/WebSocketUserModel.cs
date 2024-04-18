using FlatBackend.Controllers;
using FlatBackend.Websocket;

namespace FlatBackend.Models
{
    public class WebSocketUserModel
    {
        public Guid clientId { get; set; }
        public Guid collectionId { get; set; }
        public WebsocketManager websocketManager { get; set; }
    }
}