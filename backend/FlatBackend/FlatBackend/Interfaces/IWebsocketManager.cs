using FlatBackend.DTOs;
using FlatBackend.Models;
using System.Net.WebSockets;

namespace FlatBackend.Interfaces
{
    public interface IWebsocketManager
    {
        public void saveWebSocketOfUser( WebSocket webSocket, Guid collectionId, Guid userId );

        public void sendUpdateCollection( Guid collectionId );

        public void sendCollectionClosedInformation( Guid collectionId );

        public void sendGPSTrackCollection( TrackCollectionDto tracks, Guid collectionId );

        public void sendGPSTrack( GPSTrackDto track, Guid collectionId );

        public void removeNotConfirmedWebSocketUsers( Guid collectionId, Guid userId );

        public Task<UserModel> sendAccessRequestToBoss( AccessRequestDto request );

        public void sendAccessConfirmationToUser( AccessConfirmationDto request );
    }
}