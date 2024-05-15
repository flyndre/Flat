using FlatBackend.DTOs;
using FlatBackend.Models;
using System.Net.WebSockets;

namespace FlatBackend.Interfaces
{
    public interface IWebsocketManager
    {
        public Guid getCollectionId( WebSocket websocket );

        public void addTrackToTrackCollection( IncrementalTrackDto track, Guid collectionId );

        public void setAccessConfirmationWaiting( AccessConfirmationDto accessConfirmationDto );

        public Task sendSummaryToBoss( TrackCollectionModel tracks, Guid collectionId, Guid clientId );

        public void saveWebSocketOfUser( WebSocket webSocket, Guid collectionId, Guid userId );

        public void sendUpdateCollection( Guid collectionId );

        public void sendCollectionClosedInformation( Guid collectionId );

        public void sendGPSTrackCollection( TrackCollectionModel tracks, Guid collectionId, Guid clientId );

        public void sendGPSTrack( IncrementalTrackDto track, Guid collectionId );

        public void removeNotConfirmedWebSocketUsers( Guid collectionId, Guid userId );

        public Task<UserModel> sendAccessRequestToBoss( AccessRequestDto request );

        public void sendAccessConfirmationToUser( AccessConfirmationDto request );

        public void deleteCollection( Guid collectionId );

        public void removeWebsocketUser( WebSocket webSocket );

        public void removeWebsocketUser( Guid clientId );

        public void informBossOverLeavingOfUser( Guid collectionId, UserModel leavingUser );
    }
}