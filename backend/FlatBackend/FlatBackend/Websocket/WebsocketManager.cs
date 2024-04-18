using FlatBackend.DTOs;
using FlatBackend.Interfaces;
using FlatBackend.Models;
using MongoDB.Driver;
using System.Net.WebSockets;

namespace FlatBackend.Websocket
{
    public class WebsocketManager : IWebsocketManager
    {
        private readonly IMongoDBService _MongoDBService;

        public WebsocketManager( IMongoDBService mongoDBService )
        {
            _MongoDBService = mongoDBService;
            List<WebSocketUserModel> users = new List<WebSocketUserModel>();
        }

        public void saveWebSocketOfUser( WebSocket webSocket, Guid collectionId, Guid userId )//only if User is allready confirmed else not saved
        { }

        public async void sendUpdateCollection( Guid collectionId )
        { }

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