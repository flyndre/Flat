package de.flyndre.flat.services

import de.flyndre.flat.WebSocketClient
import de.flyndre.flat.exceptions.RequestFailedException
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.models.AccessResquestMessage
import de.flyndre.flat.models.CollectionArea
import de.flyndre.flat.models.CollectionClosedMessage
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.IncrementalTrackMessage
import de.flyndre.flat.models.RequestAccessResult
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.UserModel
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
            throw RequestFailedException("Could not create the collection $name:\n$responseString")

        }
    }


    override suspend fun closeCollection(collection: CollectionInstance) {
        val request = Request.Builder()
            .url("$baseUrl/collection/${collection.id}")
            .delete()
            .build()
        val response = restClient.newCall(request).await()
        if(!response.isSuccessful){
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not close collection ${collection.id} alias ${collection.name} \n$responseString")
        }
    }

    override suspend fun setAreaDivision(collectionId: UUID, divisions: List<CollectionArea>) {
        val url = "$baseUrl/collection/$collectionId"
        val request = Request.Builder()
            .url(url)
            .put(json.encodeToString(divisions).toRequestBody())
            .build()
        val response = restClient.newCall(request).await()
        if(!response.isSuccessful){
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not set area division on collection $collectionId areas:\n $divisions response body:\n$responseString")
        }
    }

    override suspend fun assignCollectionArea(collectionId: UUID, area: CollectionArea, clientId: UUID?) {
        val url = "$baseUrl/collection/$collectionId"
        area.clientId = clientId
        val request = Request.Builder()
            .url(url)
            .put(json.encodeToString(listOf(area)).toRequestBody())
            .build()
        val response = restClient.newCall(request).await()
        if(!response.isSuccessful){
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not assign user $clientId to area ${area.id} alias ${area.name} on collection $collectionId:\n$responseString")
        }
    }

    override suspend fun requestAccess(username: String, collectionId: UUID): RequestAccessResult {
        val url = "$baseUrl/accessrequest/$collectionId"
        val request = Request.Builder()
            .url(url)
            .post(json.encodeToString(UserModel(username,clientId)).toRequestBody())
            .build()
        val response = restClient.newCall(request).await()
        if(response.isSuccessful&&response.body !=null){
            val bodyString = response.body!!.string()
            response.close()
            return json.decodeFromString(bodyString)
        }else{
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not request access on collection $collectionId for $username alias $clientId:\n$responseString")

        }
    }

    override suspend fun giveAccess(request: AccessResquestMessage) {

        val url = "$baseUrl/AccessConfirmation/${request.collectionId}"
        val restRequest = Request.Builder()
            .url(url)
            .post(json.encodeToString(UserModel(request.username,request.userId,true)).toRequestBody())
            .build()
        val response = restClient.newCall(restRequest).await()
        if(!response.isSuccessful){
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not grant access on collection ${request.collectionId} for user ${request.username} alias ${request.userId}:\n$responseString")
        }
    }

    override suspend fun denyAccess(request: AccessResquestMessage) {
        val url = "$baseUrl/AccessConfirmation/${request.collectionId}"
        val restRequest = Request.Builder()
            .url(url)
            .post(json.encodeToString(UserModel(request.username,request.userId,false)).toRequestBody())
            .build()
        val response = restClient.newCall(restRequest).await()
        if(!response.isSuccessful){
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not deny access on collection ${request.collectionId} for user ${request.username} alias ${request.userId}:\n$responseString")
        }
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