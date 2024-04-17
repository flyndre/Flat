using FlatBackend.Controllers;
using FlatBackend.Websocket;

namespace FlatBackend.Models
{
    public class WebSocketUserModel
    {
        public UserModel User { get; set; }
        public WebsocketManager websocketManager { get; set; }
    }
}