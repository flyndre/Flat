using Newtonsoft.Json.Converters;
using System.Runtime.Serialization;
using System.Text.Json.Serialization;

namespace FlatBackend.DTOs
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum WebSocketMessageType
    {
        [EnumMember(Value = nameof(WebsocketConnection))]
        WebsocketConnection,

        [EnumMember(Value = nameof(IncrementalTrack))]
        IncrementalTrack,

        [EnumMember(Value = nameof(AccessRequest))]
        AccessRequest,

        [EnumMember(Value = nameof(CollectionClosed))]
        CollectionClosed,

        [EnumMember(Value = nameof(CollectionUpdate))]
        CollectionUpdate,

        [EnumMember(Value = nameof(Summary))]
        Summary,

        [EnumMember(Value = nameof(KeepAlive))]
        KeepAlive,

        [EnumMember(Value = nameof(LeavingUser))]
        LeavingUser,

        [EnumMember(Value = nameof(KickedUser))]
        KickedUser
    }
}