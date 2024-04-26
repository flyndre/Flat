﻿using FlatBackend.DTOs;
using FlatBackend.Interfaces;
using FlatBackend.Models;
using Microsoft.AspNetCore.Http;
using MongoDB.Driver;
using System.Collections.Concurrent;
using System.Net.WebSockets;
using System.Text;
using System.Text.Json;

namespace FlatBackend.Websocket
{
    public class WebsocketManager : IWebsocketManager
    {
        private readonly IMongoDBService _MongoDBService;
        public List<WebSocketUserModel> users;
        public BlockingCollection<WebsocketConnectionDto> connectionsWaiting;
        public BlockingCollection<AccessConfirmationDto> accessConfirmationWaiting;
        public BlockingCollection<CollectionClosedDto> collectionClosedWaiting;
        public BlockingCollection<IncrementalTrackDto> incrementalTrackWaiting;
        public BlockingCollection<CollectionUpdateDto> updateWaiting;

        public WebsocketManager( IMongoDBService mongoDBService )
        {
            _MongoDBService = mongoDBService;
            users = new List<WebSocketUserModel>();
            connectionsWaiting = new BlockingCollection<WebsocketConnectionDto>();
            accessConfirmationWaiting = new BlockingCollection<AccessConfirmationDto>();
            collectionClosedWaiting = new BlockingCollection<CollectionClosedDto>();
            incrementalTrackWaiting = new BlockingCollection<IncrementalTrackDto>();
            updateWaiting = new BlockingCollection<CollectionUpdateDto>();
        }

        public void setAccessConfirmationWaiting( AccessConfirmationDto accessConfirmationDto )
        {
            accessConfirmationWaiting.Add(accessConfirmationDto);
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
                        CollectionUpdateDto update = new CollectionUpdateDto() { collection = collection };
                        string Json = JsonSerializer.Serialize(update);
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
                    CollectionClosedDto collectionClosedDto = new CollectionClosedDto() { collectionId = collectionId };
                    string Json = JsonSerializer.Serialize(collectionClosedDto);
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                    await user.webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, "The Collection was closed so the Connection is closed too.", CancellationToken.None);
                    users.Remove(user);
                }
            }
        }

        public async void sendGPSTrackCollection( TrackCollectionDto tracks, Guid collectionId )
        {
            foreach (var user in users)
            {
                if (user.collectionId == collectionId)
                {
                    string Json = JsonSerializer.Serialize(tracks);
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                }
            }
        }

        public async void sendGPSTrack( IncrementalTrackDto track, Guid collectionId )
        {
            foreach (var user in users)
            {
                if (user.collectionId == collectionId)
                {
                    string Json = JsonSerializer.Serialize(track);
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
                if (user != null)
                {
                    var Json = JsonSerializer.Serialize(request);
                    await user.webSocket.SendAsync(Encoding.ASCII.GetBytes(Json), 0, true, CancellationToken.None);
                    var buffer = new byte[1024 * 4];
                    var response = accessConfirmationWaiting.Take();
                    UserModel model = new UserModel { clientId = response.clientId, username = request.username, accepted = response.accepted };
                    setUserConfirmation(request.collectionId, model);
                    return model;
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