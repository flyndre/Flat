using FlatBackend.DTOs;
using FlatBackend.Interfaces;
using FlatBackend.Models;
using Microsoft.AspNetCore.Http;
using MongoDB.Driver;
using System.Collections.Concurrent;
using System.Net.WebSockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

//using System.Text.Json;
using Newtonsoft.Json;
using System.Diagnostics.Metrics;
using System.IO;

namespace FlatBackend.Websocket
{
    public class WebsocketManager : IWebsocketManager
    {
        private readonly IMongoDBService _MongoDBService;
        private List<WebSocketUserModel> users;
        private List<TrackCollectionModel> trackCollections;
        public BlockingCollection<WebsocketConnectionDto> connectionsWaiting;
        public BlockingCollection<AccessConfirmationDto> accessConfirmationWaiting;
        public BlockingCollection<CollectionClosedDto> collectionClosedWaiting;
        public BlockingCollection<IncrementalTrackDto> incrementalTrackWaiting;
        public BlockingCollection<CollectionUpdateDto> updateWaiting;
        private Timer timer;

        public WebsocketManager( IMongoDBService mongoDBService )
        {
            _MongoDBService = mongoDBService;
            users = new List<WebSocketUserModel>();
            connectionsWaiting = new BlockingCollection<WebsocketConnectionDto>();
            accessConfirmationWaiting = new BlockingCollection<AccessConfirmationDto>();
            collectionClosedWaiting = new BlockingCollection<CollectionClosedDto>();
            incrementalTrackWaiting = new BlockingCollection<IncrementalTrackDto>();
            updateWaiting = new BlockingCollection<CollectionUpdateDto>();
            trackCollections = new List<TrackCollectionModel>();
            timer = new Timer(callback: new TimerCallback(x =>
            {
                List<WebSocketUserModel> deprecatedUsers = new List<WebSocketUserModel>();
                var keepAlive = new KeepAliveDto();
                string Json = JsonConvert.SerializeObject(keepAlive);
                foreach (var user in users)
                {
                    if (user != null && user.webSocket.State == WebSocketState.Open)
                    {
                        user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                    }
                    else { deprecatedUsers.Add(user); }
                }
                if (deprecatedUsers.Count > 0)
                {
                    foreach (var user in deprecatedUsers)
                    {
                        users.Remove(user);
                    }
                }
            }), state: null, dueTime: 1000, period: 30000);
        }

        public Guid getCollectionId( WebSocket websocket )
        {
            var user = users.Find(x => x.webSocket == websocket);
            if (user != null)
            {
                return user.collectionId;
            }
            else
            {
                return Guid.Empty;
            }
        }

        public void addTrackToTrackCollection( IncrementalTrackDto track, Guid collectionId )
        {
            if (trackCollections.Count == 0) { trackCollections = new List<TrackCollectionModel>() { new TrackCollectionModel() { collectionId = collectionId, tracks = new List<IncrementalTrackDto>() { track } } }; }
            var trackCollection = trackCollections.Find(x => x.collectionId == collectionId);
            if (trackCollection != null)
            {
                var oldTrack = trackCollection.tracks.Find(x => x.trackId == track.trackId);
                if (oldTrack != null)
                {
                    var index = trackCollection.tracks.IndexOf(oldTrack);
                    trackCollection.tracks[index] = track;
                }
                else
                {
                    trackCollection.tracks.Add(track);
                }
            }
            else
            {
                var newtrackCollection = new TrackCollectionModel() { collectionId = collectionId, tracks = new List<IncrementalTrackDto>() { track } };
                trackCollections.Add(newtrackCollection);
            }
        }

        public void setAccessConfirmationWaiting( AccessConfirmationDto accessConfirmationDto )
        {
            accessConfirmationWaiting.Add(accessConfirmationDto);
        }

        public async void saveWebSocketOfUser( WebSocket webSocket, Guid collectionId, Guid userId )//only if User is allready confirmed else ConnectionClosed
        {
            var collection = await _MongoDBService.GetCollection(collectionId);
            UserModel? validUser = new UserModel();
            if (collection != null)
            {
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
                    if (trackCollections.Count > 0)
                    {
                        var TrackCollection = trackCollections.Find(x => x.collectionId == collectionId);
                        if (TrackCollection != null)
                        {
                            sendGPSTrackCollection(TrackCollection, collectionId, userId);
                        }
                    }
                }
                else
                {
                    if (webSocket.State == WebSocketState.Open)
                    { await webSocket.CloseAsync(WebSocketCloseStatus.PolicyViolation, "Unauthorised connection this user isn't confirmed by the collection owner.", CancellationToken.None); }
                }
            }
            else
            { if (webSocket.State == WebSocketState.Open) await webSocket.CloseAsync(WebSocketCloseStatus.PolicyViolation, "Unauthorised connection this user isn't confirmed by the collection owner.", CancellationToken.None); }
            return;
        }

        public async void sendUpdateCollection( Guid collectionId )
        {
            var collection = await _MongoDBService.GetCollection(collectionId);
            foreach (var user in users)
            {
                if (user == null) continue;
                if (user.collectionId == collectionId && user.webSocket.State == WebSocketState.Open)
                {
                    var validUser = collection.confirmedUsers.Find(x => x.clientId == user.clientId);
                    if (validUser != null && validUser.accepted)
                    {
                        CollectionUpdateDto update = new CollectionUpdateDto() { collection = collection };
                        string Json = JsonConvert.SerializeObject(update);
                        await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                    }
                }
            }
        }

        public async void sendCollectionClosedInformation( Guid collectionId )
        {
            var collection = await _MongoDBService.GetCollection(collectionId);
            if (collection == null) return;
            var tempUsers = new List<WebSocketUserModel>();
            foreach (var user in users)
            {
                if (user == null) continue;
                if (user.collectionId == collectionId && user.webSocket.State == WebSocketState.Open)
                {
                    CollectionClosedDto collectionClosedDto = new CollectionClosedDto() { collectionId = collectionId };
                    string Json = JsonConvert.SerializeObject(collectionClosedDto);
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                    if (user.clientId == collection.clientId)
                    {
                        await sendSummaryToBoss(trackCollections.Find(x => x.collectionId == collectionId), collectionId, user.clientId);
                    }
                    try
                    {
                        if (user.webSocket.State == WebSocketState.Open)
                        { await user.webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, "The Collection was closed so the Connection is closed too.", CancellationToken.None); }
                    }
                    catch { tempUsers.Add(user); continue; }
                    tempUsers.Add(user);
                }
            }
            foreach (var user in tempUsers)
            {
                users.Remove(user);
            }
        }

        public async void sendGPSTrackCollection( TrackCollectionModel tracks, Guid collectionId, Guid clientId )
        {
            var user = users.Find(x => x.clientId == clientId && x.collectionId == collectionId);
            if (user == null || tracks == null) return;
            foreach (var track in tracks.tracks)
            {
                string Json = JsonConvert.SerializeObject(track);
                user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                Thread.Sleep(100);
            }
        }

        public async Task sendSummaryToBoss( TrackCollectionModel tracks, Guid collectionId, Guid clientId )
        {
            var collection = await _MongoDBService.GetCollection(collectionId);
            if (collection == null) return;
            var user = users.Find(x => x.clientId == clientId && x.collectionId == collectionId);
            if (user == null) return;
            SummaryModel summary = new SummaryModel() { collection = collection, trackCollection = tracks };
            string Json = JsonConvert.SerializeObject(summary);
            await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
        }

        public async void sendGPSTrack( IncrementalTrackDto track, Guid collectionId )
        {
            foreach (var user in users)
            {
                if (user == null) continue;
                if (user.collectionId == collectionId && user.webSocket.State == WebSocketState.Open)
                {
                    string Json = JsonConvert.SerializeObject(track);
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                }
            }
        }

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
                string Json;
                if (user != null && user.webSocket.State == WebSocketState.Open)
                {
                    Json = JsonConvert.SerializeObject(request);
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                    var buffer = new byte[1024 * 4];
                }
                var response = accessConfirmationWaiting.Take();
                if (response.collectionId == request.collectionId && response.clientId == request.clientId)
                {
                    UserModel model = new UserModel { clientId = response.clientId, username = request.username, accepted = response.accepted };
                    setUserConfirmation(request.collectionId, model);
                    return model;
                }
                else
                {
                    // accessConfirmationWaiting.Add(response);
                    throw new InvalidOperationException("An Error accured while setting the User confirmation please retry.");
                }
            }
            throw new InvalidOperationException("There have been an Problem. It could be that the Admin is not available...");
        }

        public async void sendAccessConfirmationToUser( AccessConfirmationDto request )
        {
            var collection = await _MongoDBService.GetCollection(request.collectionId);
            if (collection != null)
            {
                var user = users.Find(x => x.collectionId == request.collectionId && x.clientId == request.clientId);
                if (user != null)
                {
                    var Json = JsonConvert.SerializeObject(request);
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
                if (requestedUser == null) { return; }
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

        public void deleteCollection( Guid collectionId )
        {
            _MongoDBService.RemoveCollection(collectionId);
        }

        public void removeWebsocketUser( WebSocket webSocket )
        {
            var user = users.Find(x => x.webSocket == webSocket);
            if (user != null)
            {
                users.Remove(user);
            }
        }

        public async void removeWebsocketUser( Guid clientId, Guid collectionId )
        {
            var user = users.Find(x => x.clientId == clientId && x.collectionId == collectionId);
            if (user != null)
            {
                if (user.webSocket.State == WebSocketState.Open)
                {
                    await user.webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, "The Collection was closed so the Connection is closed too.", CancellationToken.None);
                }
                users.Remove(user);
            }
        }

        public async void informKickedUser( Guid clientId, Guid collectionId )
        {
            var kickedUser = users.Find(x => x.clientId == clientId && x.collectionId == collectionId);
            if (kickedUser != null)
            {
                if (kickedUser.webSocket.State == WebSocketState.Open)
                {
                    var Json = JsonConvert.SerializeObject(new KickedUserDto { message = "You have been kicked from boss Connection aborted.", type = DTOs.WebSocketMessageType.KickedUser });
                    await kickedUser.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                    if (kickedUser.webSocket.State == WebSocketState.Open)
                    { await kickedUser.webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, "The Collection was closed so the Connection is closed too.", CancellationToken.None); }
                }
            }
        }

        public async void informBossOverLeavingOfUser( Guid collectionId, UserModel leavingUser )
        {
            var collection = await _MongoDBService.GetCollection(collectionId);
            if (collection != null)
            {
                var user = users.Find(x => x.collectionId == collection.id && x.clientId == collection.clientId);
                if (user != null && user.webSocket.State == WebSocketState.Open)
                {
                    var Json = JsonConvert.SerializeObject(new LeavingUserDto { user = leavingUser, type = DTOs.WebSocketMessageType.LeavingUser });
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                }
            }
        }
    }
}