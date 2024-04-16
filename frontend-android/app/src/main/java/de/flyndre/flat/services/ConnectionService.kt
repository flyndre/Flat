package de.flyndre.flat.services

import de.flyndre.flat.WebSocketClient
import de.flyndre.flat.exceptions.OpenCollectionException
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
import io.github.dellisd.spatialk.geojson.MultiPolygon
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.gildor.coroutines.okhttp.await
import java.util.UUID

class ConnectionService(
    private var baseUrl:String,
    private val clientId:UUID,
    override val onAccessRequest: ArrayList<(AccessResquestMessage) -> Unit> = arrayListOf(),
    override var onCollectionClosed: ArrayList<(CollectionClosedMessage) -> Unit> = arrayListOf(),
    override var onTrackUpdate: ArrayList<(IncrementalTrackMessage) -> Unit> = arrayListOf(),
):IConnectionService {

    private val restClient = OkHttpClient.Builder().build()
    private val webSocketClient: WebSocketClient = WebSocketClient.getInstance()
    private val socketListener = object : WebSocketClient.SocketListener {
        override fun onMessage(message: String) {
            val obj = Json.decodeFromString<WebSocketMessage>(message)
            when(obj.type){
                WebSocketMessageType.IncrementalTrack -> onTrackUpdate.stream().forEach { x->x(obj as IncrementalTrackMessage) }
                WebSocketMessageType.AccessRequest -> onAccessRequest.stream().forEach { x->x(obj as AccessResquestMessage) }
                WebSocketMessageType.CollectionClosed -> onCollectionClosed.stream().forEach { x->x(obj as CollectionClosedMessage) }
            }
        }
    }
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun openCollection(name: String, area: MultiPolygon
    ): CollectionInstance {
        val request = Request.Builder()
            .url("$baseUrl/collection")
            .post(Json.encodeToString(CollectionInstance(name,clientId,area)).toRequestBody("application/json".toMediaType()))
            .build()
        val response = restClient.newCall(request).await()
        if(response.isSuccessful&&response.body !=null){
            val bodyString = response.body!!.string()
            response.close()
            return json.decodeFromString(bodyString)
        }else{
            val responseString = response.body?.string()
            response.close()
            throw OpenCollectionException("Could not create the collection:\n$responseString")

        }
    }


    override suspend fun closeCollection(collection: CollectionInstance) {
        val request = Request.Builder()
            .url("$baseUrl/api/rest/collection/${collection.id}")
            .delete()
            .build()
        val result = restClient.newCall(request).await()
        if(!result.isSuccessful){
            TODO()
        }
    }

    override suspend fun setAreaDivision(collectionId: UUID, divisions: List<CollectionArea>) {
        val url = "$baseUrl/api/rest/collection/$collectionId"
        val request = Request.Builder()
            .url(url)
            .put(json.encodeToString(divisions).toRequestBody())
            .build()
        restClient.newCall(request).await()
    }

    override suspend fun assignCollectionArea(collectionId: UUID, area: CollectionArea, clientId: UUID?) {
        TODO("Not yet implemented")
    }

    override suspend fun requestAccess(username: String, collectionId: UUID): RequestAccessResult {
        TODO("Not yet implemented")
    }

    override suspend fun giveAccess(request: AccessResquestMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun denyAccess(request: AccessResquestMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun leaveCollection(collection: CollectionInstance) {
        TODO("Not yet implemented")
    }

    override suspend fun sendTrackUpdate(track: Track) {
        val message = Json.encodeToString(IncrementalTrackMessage(track.trackId.toString(),track.toLineString()))
        webSocketClient.sendMessage(message)
    }

    override suspend fun openWebsocket(onAccessRequest: ((AccessResquestMessage) -> Unit)?,
                                       onCollectionClosed: ((CollectionClosedMessage) -> Unit)?,
                                       onTrackUpdate: ((IncrementalTrackMessage) -> Unit)?) {

        onAccessRequest?.let { addOnAccessRequest(it) }
        onCollectionClosed?.let { addOnCollectionClosed(it) }
        onTrackUpdate?.let { addOnTrackUpdate(it) }
        webSocketClient.setSocketUrl("$baseUrl/ws")
        webSocketClient.setListener(socketListener)
        webSocketClient.connect()
    }

    override suspend fun closeWebsocket() {
        webSocketClient.disconnect()
    }

    override fun addOnAccessRequest(callback: (AccessResquestMessage) -> Unit) {
        onAccessRequest.add(callback)
    }

    override fun addOnCollectionClosed(callback: (CollectionClosedMessage) -> Unit) {
        onCollectionClosed.add(callback)
    }

    override fun addOnTrackUpdate(callback: (IncrementalTrackMessage) -> Unit) {
        onTrackUpdate.add(callback)
    }
}