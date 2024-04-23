using System.Net.WebSockets;
using System.Text;
using System.Text.Json;
using FlatBackend.DTOs;
using FlatBackend.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace FlatBackend.Controllers
{
    public class WebsocketController : ControllerBase
    {
        private static IWebsocketManager _WebsocketManager;
        private static DtoJsonCategoriser _DtoJsonCategoriser;
        private static JavaScriptSerializer

        public WebsocketController( IWebsocketManager websocketManager )
        {
            _WebsocketManager = websocketManager;
            _DtoJsonCategoriser = new DtoJsonCategoriser();
        }

        [Route("/ws")]
        [HttpGet]
        public async Task Get()
        {
            if (HttpContext.WebSockets.IsWebSocketRequest)
            {
                using var webSocket = await HttpContext.WebSockets.AcceptWebSocketAsync();

                await Echo(webSocket);
            }
            else
            {
                HttpContext.Response.StatusCode = StatusCodes.Status400BadRequest;
            }
        }

        private static async Task Echo( WebSocket webSocket )
        {
            var buffer = new byte[1024 * 4];
            var receiveResult = await webSocket.ReceiveAsync(
                new ArraySegment<byte>(buffer), CancellationToken.None);

            while (!receiveResult.CloseStatus.HasValue)
            {
                var Json = Encoding.ASCII.GetString(buffer);
                Json = new string(Json.Where(c => c != '\x00').ToArray());

                if (_DtoJsonCategoriser.isWebsocketConnectionDto(Json))
                {
                    var webSocketUser = JsonSerializer.Deserialize<WebsocketConnectionDto>(Json);
                    _WebsocketManager.saveWebSocketOfUser(webSocket, webSocketUser.collectionId, webSocketUser.clientId);
                    await webSocket.SendAsync(
                        new ArraySegment<byte>(buffer, 0, receiveResult.Count),
                        receiveResult.MessageType,
                        receiveResult.EndOfMessage,
                        CancellationToken.None);
                }

                //await webSocket.SendAsync(
                //    new ArraySegment<byte>(buffer, 0, receiveResult.Count),
                //    receiveResult.MessageType,
                //    receiveResult.EndOfMessage,
                //    CancellationToken.None);

                receiveResult = await webSocket.ReceiveAsync(
                    new ArraySegment<byte>(buffer), CancellationToken.None);
            }

            await webSocket.CloseAsync(
                receiveResult.CloseStatus.Value,
                receiveResult.CloseStatusDescription,
                CancellationToken.None);
        }
    }
}