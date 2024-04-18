using FlatBackend.DTOs;
using FlatBackend.Interfaces;
using FlatBackend.Models;
using MongoDB.Driver;
using System.Net.WebSockets;
using System.Text;
using System.Text.Json;

namespace FlatBackend.Websocket
{
    public class WebsocketManager : IWebsocketManager
    {
        private readonly IMongoDBService _MongoDBService;
        public List<WebSocketUserModel> users;

        public WebsocketManager( IMongoDBService mongoDBService )
        {
            _MongoDBService = mongoDBService;
            users = new List<WebSocketUserModel>();
        }

        public async void saveWebSocketOfUser( WebSocket webSocket, Guid collectionId, Guid userId )//only if User is allready confirmed else not saved
        {
            var collection = await _MongoDBService.GetCollection(collectionId);
            var validUser = collection.confirmedUsers.Find(x => x.clientId == userId);
            if (validUser != null && validUser.accepted || collection.clientId == userId)
            {
                WebSocketUserModel newUser = new WebSocketUserModel { webSocket = webSocket, collectionId = collectionId, clientId = userId };
                var index = users.IndexOf(newUser);
                if (index > 0)
                {
                    users[index] = newUser;
                }
                else
                {
                    users.Add(newUser);
                }
            }
            else
            {
                await webSocket.CloseAsync(WebSocketCloseStatus.PolicyViolation, "Unauthorised connection this user isn't confirmed by the collection owner.", CancellationToken.None);
            }
        }

        public async void sendUpdateCollection( Guid collectionId )
        {
            var collection = await _MongoDBService.GetCollection(collectionId);
            foreach (var user in users)
            {
                if (user.collectionId == collectionId)
                {
                    var validUser = collection.confirmedUsers.Find(x => x.clientId == user.clientId);
                    if (validUser != null && validUser.accepted)
                    {
                        string Json = JsonSerializer.Serialize(collection);
                        await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                    }
                }
            }
        }

        public async void sendCollectionClosedInformation( Guid collectionId )
        {
            var collection = await _MongoDBService.GetCollection(collectionId);
            foreach (var user in users)
            {
                if (user.collectionId == collectionId)
                {
                    CollectionClosedDto collectionClosedDto = new CollectionClosedDto();
                    string Json = JsonSerializer.Serialize(collectionClosedDto);
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                    await user.webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, "The Collection was closed so the Connection is closed too.", CancellationToken.None);
                    users.Remove(user);
                }
            }
        }

        public async void sendGPSTrackCollection( TrackCollectionDto tracks, Guid collectionId )
        { }

        public async void sendGPSTrack( GPSTrackDto track, Guid collectionId )
        { }

        public void removeNotConfirmedWebSocketUsers( Guid collectionId, Guid userId )
        {
            var user = users.Find(x => x.clientId == userId && x.collectionId == collectionId);
            if (user != null)
            {
                users.Remove(user);
            }
        }

        public async void sendAccessRequestToBoss( AccessRequestDto request )
        {
            var collection = await _MongoDBService.GetCollection(request.collectionId);
            if (collection != null)
            {
                var user = users.Find(x => x.collectionId == collection.id && x.clientId == collection.clientId);
                if (user != null)
                {
                    var Json = JsonSerializer.Serialize(request);
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                }
            }
        }
    }
}