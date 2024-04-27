using System.Net.WebSockets;
using System.Text;
using FlatBackend.DTOs;
using FlatBackend.Interfaces;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using WebSocketMessageType = FlatBackend.DTOs.WebSocketMessageType;

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

        [Route("api/ws")]
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
                var message = JsonConvert.DeserializeObject<WebSocketMessage>(Json);
                if(message != null)
                {
                    switch (message.type)
                    {
                        case WebSocketMessageType.WebsocketConnection:
                            var webSocketUser = JsonConvert.DeserializeObject<WebsocketConnectionDto>(Json);
                            _WebsocketManager.saveWebSocketOfUser(webSocket, webSocketUser.collectionId, webSocketUser.clientId);
                            await webSocket.SendAsync(
                                new ArraySegment<byte>(buffer, 0, receiveResult.Count),
                                receiveResult.MessageType,
                                receiveResult.EndOfMessage,
                                CancellationToken.None);
                            break;

                        case WebSocketMessageType.IncrementalTrack:
                            var track = JsonConvert.DeserializeObject<IncrementalTrackDto>(Json);
                            var collectionId = _WebsocketManager.getCollectionId(webSocket);
                            //_WebsocketManager.addTrackToTrackCollection(track, collectionId);
                            _WebsocketManager.sendGPSTrack(track, collectionId);
                            break;

                        case WebSocketMessageType.AccessRequest:
                            _WebsocketManager.setAccessConfirmationWaiting(JsonConvert.DeserializeObject<AccessConfirmationDto>(Json));
                            break;

                        case WebSocketMessageType.CollectionClosed://CollectionClosed kp was das hier tun soll
                            var result = JsonConvert.DeserializeObject<CollectionClosedDto>(Json);
                            _WebsocketManager.sendCollectionClosedInformation(result.collectionId);
                            break;

                        case WebSocketMessageType.CollectionUpdate://CollectionUpdate kp was das hier tun soll
                            break;

                        default:
                            break;
                    };
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