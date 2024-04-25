using System.Linq;
using System.Net.WebSockets;
using System.Runtime.InteropServices.JavaScript;
using System.Text;
using System.Text.Json;
using System.Text.RegularExpressions;
using FlatBackend.DTOs;
using FlatBackend.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace FlatBackend.Controllers
{
    public class WebsocketController : ControllerBase
    {
        private static IWebsocketManager _WebsocketManager;
        private static DtoJsonCategoriser _DtoJsonCategoriser;

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
                string Json = Encoding.ASCII.GetString(buffer);
                Json = new string(Json.Where(c => c != '\x00').ToArray());
                var index = Json.IndexOf("\"type\":");
                string z = Json.Substring(index + 8, 1);

                if (int.TryParse(z, out int type))
                    switch (type)
                    {
                        case 0:
                            var webSocketUser = JsonSerializer.Deserialize<WebsocketConnectionDto>(Json);
                            _WebsocketManager.saveWebSocketOfUser(webSocket, webSocketUser.collectionId, webSocketUser.clientId);
                            await webSocket.SendAsync(
                                new ArraySegment<byte>(buffer, 0, receiveResult.Count),
                                receiveResult.MessageType,
                                receiveResult.EndOfMessage,
                                CancellationToken.None);
                            break;

                        case 1://IncrementalTrack
                            break;

                        case 2://AccessRequest
                            _WebsocketManager.setAccessConfirmationWaiting(JsonSerializer.Deserialize<AccessConfirmationDto>(Json));
                            break;

                        case 3://CollectionClosed kp was das hier tun soll
                            var result = JsonSerializer.Deserialize<CollectionClosedDto>(Json);
                            _WebsocketManager.sendCollectionClosedInformation(result.collectionId);
                            break;

                        case 4://CollectionUpdate kp was das hier tun soll
                            break;

                        default:
                            break;
                    };

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