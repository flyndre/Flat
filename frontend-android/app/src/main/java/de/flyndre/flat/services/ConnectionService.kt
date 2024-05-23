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
import de.flyndre.flat.models.KickedUserMessage
import de.flyndre.flat.models.LeavingUserMessage
import de.flyndre.flat.models.RequestAccessResult
import de.flyndre.flat.models.SummaryMessage
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
    override val onUserLeaved: ArrayList<(LeavingUserMessage) -> Unit> = arrayListOf(),
    override val onUserKicked: ArrayList<(KickedUserMessage) -> Unit> = arrayListOf(),
    override val onSummary: ArrayList<(SummaryMessage) -> Unit> = arrayListOf(),
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
                WebSocketMessageType.IncrementalTrack -> onTrackUpdate.forEach { it(json.decodeFromString<IncrementalTrackMessage>(message)) }
                WebSocketMessageType.AccessRequest -> onAccessRequest.forEach { it(json.decodeFromString<AccessResquestMessage>(message)) }
                WebSocketMessageType.CollectionClosed -> onCollectionClosed.forEach { it(json.decodeFromString<CollectionClosedMessage>(message)) }
                WebSocketMessageType.CollectionUpdate -> onCollectionUpdate.forEach { it(json.decodeFromString<CollectionUpdateMessage>(message)) }
                WebSocketMessageType.LeavingUser -> onUserLeaved.forEach { (it(json.decodeFromString<LeavingUserMessage>(message))) }
                WebSocketMessageType.KickedUser -> onUserKicked.forEach { (it(json.decodeFromString<KickedUserMessage>(message))) }
                WebSocketMessageType.Summary -> onSummary.forEach { (it(json.decodeFromString<SummaryMessage>(message))) }
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
        val url = "$restBasePath/leaveCollection/${collection.id}?clientId=$clientId"
        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody("application/json".toMediaType()))
            .build()
        val response = restClient.newCall(request).await()
        if(!response.isSuccessful){
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not close collection ${collection.id} alias ${collection.name}:\n $responseString")
        }
    }

    override suspend fun kickUser(collection: CollectionInstance, userId: UUID) {
        val url = "$restBasePath/removeUser/${collection.id}?clientId=$userId&bossId=$clientId"
        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody("application/json".toMediaType()))
            .build()
        val response = restClient.newCall(request).await()
        if(!response.isSuccessful){
            val responseString = response.body?.string()
            response.close()
            throw RequestFailedException("Could not kick user $userId from collection ${collection.id} alias ${collection.name}:\n $responseString")
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
        onCollectionUpdate: ((CollectionUpdateMessage) -> Unit)?,
        onUserLeaved: ((LeavingUserMessage)->Unit)?,
        onUserKicked: ((KickedUserMessage)->Unit)?,
        onSummary: ((SummaryMessage)->Unit)?
    ) {

        onAccessRequest?.let { addOnAccessRequest(it) }
        onCollectionClosed?.let { addOnCollectionClosed(it) }
        onTrackUpdate?.let { addOnTrackUpdate(it) }
        onCollectionUpdate?.let { addOnCollectionUpdate(it) }
        onUserLeaved?.let { addOnUserLeaved(it) }
        onUserKicked?.let { addOnUserKicked(it) }
        onSummary?.let { addOnSummary(it) }
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

    override fun addOnUserLeaved(callback: (LeavingUserMessage) -> Unit) {
        onUserLeaved.add(callback)
    }

    override fun addOnUserKicked(callback: (KickedUserMessage) -> Unit) {
        onUserKicked.add(callback)
    }

    override fun addOnSummary(callback: (SummaryMessage) -> Unit) {
        onSummary.add(callback)
    }
}