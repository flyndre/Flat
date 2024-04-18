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
            if (validUser != null && validUser.accepted)
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
        { }

        public async void sendGPSTrackCollection( TrackCollectionDto tracks, Guid collectionId )
        { }

        public async void sendGPSTrack( GPSTrackDto track, Guid collectionId )
        { }

        public void removeNotConfirmedWebSocketUsers( Guid collectionId, Guid userId )
        { }

        public async void sendAccessRequestToBoss( AccessRequestDto request )
        { }

        public async void sendMessageBack()
        { }
    }
}