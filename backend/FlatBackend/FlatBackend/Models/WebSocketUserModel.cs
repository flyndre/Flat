using FlatBackend.Controllers;

namespace FlatBackend.Models
{
    public class WebSocketUserModel
    {
        public UserModel User { get; set; }
        public WebsocketManager websocketManager { get; set; }
    }
}