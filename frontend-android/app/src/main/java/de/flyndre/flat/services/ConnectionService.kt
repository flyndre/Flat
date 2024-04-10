package de.flyndre.flat.services

import de.flyndre.flat.WebSocketClient
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.models.AccessResquestMessage
import de.flyndre.flat.models.CollectionArea
import de.flyndre.flat.models.CollectionClosedMessage
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.IncrementalTrackMessage
import de.flyndre.flat.models.RequestAccessResult
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.WebSocketMessage
import de.flyndre.flat.models.WebSocketMessageType
import io.github.dellisd.spatialk.geojson.Polygon
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID

class ConnectionService(
    var baseUrl:String,
    var clientId:UUID = UUID.randomUUID(),
    override var onAccessResquest: ((AccessResquestMessage) -> Unit)? =null,
    override var onCollectionClosed: ((CollectionClosedMessage) -> Unit)? =null,
    override var onTrackUpdate: ((IncrementalTrackMessage) -> Unit)? =null
):IConnectionService {

    private var restClient = OkHttpClient()
    private var webSocketClient: WebSocketClient = WebSocketClient.getInstance()
    private val socketListener = object : WebSocketClient.SocketListener {
        override fun onMessage(message: String) {
            var obj = Json.decodeFromString<WebSocketMessage>(message)
            when(obj.type){
                WebSocketMessageType.IncrementalTrack -> onTrackUpdate?.let { it( obj as IncrementalTrackMessage) }
                WebSocketMessageType.AccessRequest -> onAccessResquest?.let { it (obj as AccessResquestMessage) }
                WebSocketMessageType.CollectionClosed -> onCollectionClosed?.let { it (obj as CollectionClosedMessage) }
            }
        }
    }

    init {
        webSocketClient.setSocketUrl(baseUrl)
        webSocketClient.setListener(socketListener)
        webSocketClient.connect()
    }





    override fun openCollection(
        name: String,
        area: Polygon,
        divisions: List<CollectionArea>
    ): CollectionInstance {
        var request = Request.Builder()
            .url("$baseUrl/api/rest/collection")
            .post(FormBody.Builder().build())
            .build()
        var result = restClient.newCall(request).execute()
        if(result.isSuccessful){
            return CollectionInstance("test-$name", UUID.randomUUID(),area, arrayListOf())
        }else{
            TODO()
        }
    }

    override fun closeCollection(collection: CollectionInstance) {
        var request = Request.Builder()
            .url("$baseUrl/api/rest/collection/${collection.id}")
            .delete()
            .build()
        var result = restClient.newCall(request).execute()
        if(!result.isSuccessful){
            TODO()
        }
    }

    override fun setAreaDivision(divisions: List<CollectionArea>) {
        TODO("Not yet implemented")
    }

    override fun assignCollectionArea(area: CollectionArea, clientId: UUID?) {
        TODO("Not yet implemented")
    }

    override fun requestAccess(username: String, collectionId: UUID): RequestAccessResult {
        TODO("Not yet implemented")
    }

    override fun giveAccess(request: AccessResquestMessage) {
        TODO("Not yet implemented")
    }

    override fun denyAccess(request: AccessResquestMessage) {
        TODO("Not yet implemented")
    }

    override fun leaveCollection(collection: CollectionInstance) {
        TODO("Not yet implemented")
    }

    override fun sendTrackUpdate(track: Track) {
        var message = Json.encodeToString(IncrementalTrackMessage(track.trackId.toString(),track.toLineString()))
        webSocketClient.sendMessage(message)
    }
}