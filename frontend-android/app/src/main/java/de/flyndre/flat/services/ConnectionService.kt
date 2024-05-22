package de.flyndre.flat.services

import de.flyndre.flat.WebSocketClient
import de.flyndre.flat.exceptions.RequestFailedException
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ISettingService
import de.flyndre.flat.models.AccessResquestMessage
import de.flyndre.flat.models.CollectionArea
import de.flyndre.flat.models.CollectionClosedMessage
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.CollectionUpdateMessage
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
    private val settingService: ISettingService,
    override val onAccessRequest: ArrayList<(AccessResquestMessage) -> Unit> = arrayListOf(),
    override val onCollectionClosed: ArrayList<(CollectionClosedMessage) -> Unit> = arrayListOf(),
    override val onTrackUpdate: ArrayList<(IncrementalTrackMessage) -> Unit> = arrayListOf(),
    override val onCollectionUpdate: ArrayList<(CollectionUpdateMessage) -> Unit> = arrayListOf(),
):IConnectionService {
    private val restBasePath = settingService.getServerBaseUrl()+"/rest"
    private val websocketBasePath = (settingService.getServerBaseUrl()+"/ws").replace("http","ws")
    private val clientId = settingService.getClientId()
    private val restClient = OkHttpClient.Builder().build()
    private val webSocketClient: WebSocketClient = WebSocketClient.getInstance()

    private val socketListener = object : WebSocketClient.SocketListener {
        override fun onMessage(message: String) {
            val obj = json.decodeFromString<WebSocketMessage>(message)
            when(obj.type){
                WebSocketMessageType.IncrementalTrack -> onTrackUpdate.stream().forEach { x->x(json.decodeFromString<IncrementalTrackMessage>(message)) }
                WebSocketMessageType.AccessRequest -> onAccessRequest.stream().forEach { x->x(json.decodeFromString<AccessResquestMessage>(message)) }
                WebSocketMessageType.CollectionClosed -> onCollectionClosed.stream().forEach { x->x(json.decodeFromString<CollectionClosedMessage>(message)) }
                WebSocketMessageType.CollectionUpdate -> onCollectionUpdate.stream().forEach { x->x(json.decodeFromString<CollectionUpdateMessage>(message)) }
                else -> {}
            }
        }
    }
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun openCollection(name: String, area: MultiPolygon
    ): CollectionInstance {
        val jsonString = json.encodeToString(CollectionInstance(name,clientId,area))
        val request = Request.Builder()
            .url("$restBasePath/collection")
            .post(jsonString.toRequestBody("application/json".toMediaType()))
            .build()
        val response = restClient.newCall(request).await()
        if(response.isSuccessful&&response.body !=null){
            val bodyString = response.body!!.string()
            val collection: CollectionInstance = json.decodeFromString(bodyString)
            response.close()
            openWebsocket(collection.id!!)
            return collection
        }else{
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not create the collection $name:\n$responseString")

        }
    }


    override suspend fun closeCollection(collection: CollectionInstance) {
        val request = Request.Builder()
            .url("$restBasePath/collection/${collection.id}")
            .delete()
            .build()
        val response = restClient.newCall(request).await()
        if(!response.isSuccessful){
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not close collection ${collection.id} alias ${collection.name} \n$responseString")
        }
    }

    override suspend fun setAreaDivision(collectionId: UUID, divisions: List<CollectionArea>):CollectionInstance {
        val url = "$restBasePath/collection/$collectionId"
        val jsonString = json.encodeToString(divisions)
        val request = Request.Builder()
            .url(url)
            .put(jsonString.toRequestBody("application/json".toMediaType()))
            .build()
        val response = restClient.newCall(request).await()
        if(response.isSuccessful&&response.body !=null){
            val bodyString = response.body!!.string()
            val collection: CollectionInstance = json.decodeFromString(bodyString)
            response.close()
            openWebsocket(collection.id!!)
            return collection
        }else{
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could set the divisions :\n$responseString")

        }
    }

    override suspend fun assignCollectionArea(collectionId: UUID, area: CollectionArea, clientId: UUID?) {
        val url = "$restBasePath/collection/$collectionId"
        area.clientId = clientId
        val request = Request.Builder()
            .url(url)
            .put(json.encodeToString(listOf(area)).toRequestBody("application/json".toMediaType()))
            .build()
        val response = restClient.newCall(request).await()
        if(!response.isSuccessful){
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not assign user $clientId to area ${area.id} alias ${area.name} on collection $collectionId:\n$responseString")
        }
    }

    override suspend fun requestAccess(username: String, collectionId: UUID): RequestAccessResult {
        val url = "$restBasePath/accessrequest/$collectionId"
        val request = Request.Builder()
            .url(url)
            .post(json.encodeToString(UserModel(username,settingService.getClientId())).toRequestBody("application/json".toMediaType()))
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

    override suspend fun giveAccess(request: AccessResquestMessage):CollectionInstance {
        request.accepted=true
        val message = json.encodeToString(request)
        webSocketClient.sendMessage(message)
        val url = "$restBasePath/Collection/${request.collectionId}?userId=$clientId"
        val restRequest = Request.Builder()
            .url(url)
            .get()
            .build()
        val response = restClient.newCall(restRequest).await()
        if(response.isSuccessful&&response.body !=null){
            val bodyString = response.body!!.string()
            val collection: CollectionInstance = json.decodeFromString(bodyString)
            response.close()
            return collection
        }else{
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not give Access to User ${request.clientId} alias ${request.username}:\n $responseString")

        }
    }

    override suspend fun denyAccess(request: AccessResquestMessage):CollectionInstance {
        request.accepted = false
        val message = json.encodeToString(request)
        webSocketClient.sendMessage(message)
        val url = "$restBasePath/Collection/${request.collectionId}"
        val restRequest = Request.Builder()
            .url(url)
            .get()
            .build()
        val response = restClient.newCall(restRequest).await()
        if(response.isSuccessful&&response.body !=null){
            val bodyString = response.body!!.string()
            val collection: CollectionInstance = json.decodeFromString(bodyString)
            response.close()
            return collection
        }else{
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not deny Access to User ${request.clientId} alias ${request.username}:\n $responseString")
        }
    }

    override suspend fun leaveCollection(collection: CollectionInstance) {
        webSocketClient.disconnect()
        if(collection.clientId.equals(clientId)){
            val url = "$restBasePath/Collection/${collection.id}"
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()
            val response = restClient.newCall(request).await()
            if(!response.isSuccessful){
                val responseString = response.body?.string()
                response.close()
                throw RequestFailedException("Could not close collection ${collection.id} alias ${collection.name}:\n $responseString")
            }
        }
    }

    override suspend fun sendTrackUpdate(track: Track) {
        val message = Json.encodeToString(IncrementalTrackMessage(track.trackId,clientId,track.toLineString()))
        webSocketClient.sendMessage(message)
    }

    override suspend fun openWebsocket(
        collectionId: UUID,
        onAccessRequest: ((AccessResquestMessage) -> Unit)?,
        onCollectionClosed: ((CollectionClosedMessage) -> Unit)?,
        onTrackUpdate: ((IncrementalTrackMessage) -> Unit)?,
        onCollectionUpdate: ((CollectionUpdateMessage) -> Unit)?
    ) {

        onAccessRequest?.let { addOnAccessRequest(it) }
        onCollectionClosed?.let { addOnCollectionClosed(it) }
        onTrackUpdate?.let { addOnTrackUpdate(it) }
        onCollectionUpdate?.let { addOnCollectionUpdate(it) }
        webSocketClient.setSocketUrl(websocketBasePath)
        webSocketClient.setListener(socketListener)
        webSocketClient.setUserId(clientId)
        webSocketClient.setCollectionId(collectionId)
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

    override fun addOnCollectionUpdate(callback: (CollectionUpdateMessage) -> Unit) {
        onCollectionUpdate.add(callback)
    }
}