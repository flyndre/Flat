using System.Text.Json;

namespace FlatBackend.DTOs
{
    public class DtoJsonCategoriser
    {
        public DtoJsonCategoriser()
        { }

        public bool isAccessConfirmationDto( string accessConfirmationDtoJson )
        {
            try
            {
                AccessConfirmationDto result = JsonSerializer.Deserialize<AccessConfirmationDto>(accessConfirmationDtoJson);
                if (result == null)
                {
                    return false;
                }
                return true;
            }
            catch (Exception ex)
            {
                return false;
            }
        }

        public bool isAccessRequestDto( string accessRequestDtoJson )
        {
            try
            {
                AccessRequestDto result = JsonSerializer.Deserialize<AccessRequestDto>(accessRequestDtoJson);
                if (result == null)
                {
                    return false;
                }
                return true;
            }
            catch (Exception ex)
            {
                return false;
            }
        }

        public bool isGPSTrackDto( string gpsTrackDtoJson )
        {
            try
            {
                GPSTrackDto result = JsonSerializer.Deserialize<GPSTrackDto>(gpsTrackDtoJson);
                if (result == null)
                {
                    return false;
                }
                return true;
            }
            catch (Exception ex)
            {
                return false;
            }
        }

        public bool isWebsocketConnectionDto( string websocketConnectionDtoJson )
        {
            try
            {
                WebsocketConnectionDto result = JsonSerializer.Deserialize<WebsocketConnectionDto>(websocketConnectionDtoJson);
                if (result == null)
                {
                    return false;
                }
                return true;
            }
            catch (Exception ex)
            {
                return false;
            }
        }
    }
}