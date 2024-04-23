using FlatBackend.DTOs;
using FlatBackend.Interfaces;
using FlatBackend.Models;
using Microsoft.AspNetCore.Http;
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
        public bool blocked = false;

        public WebsocketManager( IMongoDBService mongoDBService )
        {
            _MongoDBService = mongoDBService;
            users = new List<WebSocketUserModel>();
        }

        public async void saveWebSocketOfUser( WebSocket webSocket, Guid collectionId, Guid userId )//only if User is allready confirmed else not saved
        {
            var collection = await _MongoDBService.GetCollection(collectionId);
            UserModel? validUser = new UserModel();
            if (collection.confirmedUsers != null)
            { validUser = collection.confirmedUsers.Find(x => x.clientId == userId); }
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
            return;
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

        public async void sendGPSTrack( IncrementalTrackDto track, Guid collectionId )
        { }

        public void removeNotConfirmedWebSocketUsers( Guid collectionId, Guid userId )
        {
            var user = users.Find(x => x.clientId == userId && x.collectionId == collectionId);
            if (user != null)
            {
                users.Remove(user);
            }
        }

        public async Task<UserModel> sendAccessRequestToBoss( AccessRequestDto request )
        {
            var collection = await _MongoDBService.GetCollection(request.collectionId);
            if (collection != null)
            {
                var user = users.Find(x => x.collectionId == collection.id && x.clientId == collection.clientId);
                if (user != null)
                {
                    var Json = JsonSerializer.Serialize(request);
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                    var buffer = new byte[1024 * 4];
                    var response = await user.webSocket.ReceiveAsync(buffer, CancellationToken.None);
                    var JsonResult = Encoding.ASCII.GetString(buffer);
                    JsonResult = new string(JsonResult.Where(c => c != '\x00').ToArray());
                    var result = JsonSerializer.Deserialize<UserModel>(JsonResult);
                    setUserConfirmation(request.collectionId, result);
                    return result;
                }
            }
            return null;
        }

        public async void sendAccessConfirmationToUser( AccessConfirmationDto request )
        {
            var collection = await _MongoDBService.GetCollection(request.collectionId);
            if (collection != null)
            {
                var user = users.Find(x => x.collectionId == request.collectionId && x.clientId == request.clientId);
                if (user != null)
                {
                    var Json = JsonSerializer.Serialize(request);
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                }
            }
        }

        public async void setUserConfirmation( Guid collectionId, UserModel user )
        {
            var collection = await _MongoDBService.GetCollection(collectionId);
            if (collection != null && user != null)
            {
                UserModel? requestedUser = collection.requestedAccess.Find(e => e.clientId == user.clientId);
                UserModel? validUser = collection.confirmedUsers.Find(x => x.clientId == user.clientId);
                if (validUser == null)
                {
                    requestedUser.accepted = user.accepted;
                    collection.requestedAccess.Remove(requestedUser);
                    collection.confirmedUsers.Add(requestedUser);
                }
                else
                {
                    var index = collection.confirmedUsers.IndexOf(validUser);
                    collection.confirmedUsers[index] = user;
                }
                _MongoDBService.ChangeCollection(collection);
                var result = await _MongoDBService.GetCollection(collectionId);
                sendUpdateCollection(collectionId);
            }
        }
    }
}