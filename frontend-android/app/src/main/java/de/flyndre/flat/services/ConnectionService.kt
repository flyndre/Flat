package de.flyndre.flat.services

import de.flyndre.flat.WebSocketClient
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.models.AccessResquest
import de.flyndre.flat.models.CollectionArea
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.IncrementalTrackMessage
import de.flyndre.flat.models.RequestAccessResult
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.WebSocketMessage
import de.flyndre.flat.models.WebSocketMessageType
import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Polygon
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class ConnectionService(
    baseurl:String, clientId:UUID = UUID.randomUUID(),
    override var onAccessResquest: ((AccessResquest) -> Unit)? =null,
    override var onCollectionClosed: (() -> Unit)? =null,
    override var onTrackUpdate: ((IncrementalTrackMessage) -> Unit)? =null
):IConnectionService {

    var webSocketClient: WebSocketClient
    private val socketListener = object : WebSocketClient.SocketListener {
        override fun onMessage(message: String) {
            var obj = Json.decodeFromString<WebSocketMessage>(message)
            when(obj.type){
                WebSocketMessageType.IncrementalTrack -> onTrackUpdate?.let { it( obj as IncrementalTrackMessage) }
            }
        }
    }
    init {
        webSocketClient = WebSocketClient.getInstance()
        webSocketClient.setSocketUrl(baseurl)
        webSocketClient.setListener(socketListener)
        webSocketClient.connect()
    }


    override fun openCollection(
        name: String,
        area: Polygon,
        divisions: List<CollectionArea>
    ): CollectionInstance {
        onTrackUpdate?.let { it(IncrementalTrackMessage("", LineString(Position(0.0,0.0), Position(0.0,0.0)))) }
        TODO("Not yet implemented")
    }

    override fun closeCollection(collection: CollectionInstance) {
        TODO("Not yet implemented")
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

    override fun giveAccess(request: AccessResquest) {
        TODO("Not yet implemented")
    }

    override fun denyAccess(request: AccessResquest) {
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