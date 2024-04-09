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
import io.github.dellisd.spatialk.geojson.Polygon
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.cert.X509Certificate
import java.util.UUID
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ConnectionService(
    var baseUrl:String,
    var clientId:UUID = UUID.randomUUID(),
    override var onAccessResquest: ((AccessResquestMessage) -> Unit)? =null,
    override var onCollectionClosed: ((CollectionClosedMessage) -> Unit)? =null,
    override var onTrackUpdate: ((IncrementalTrackMessage) -> Unit)? =null
):IConnectionService {

    private lateinit var restClient:OkHttpClient
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
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(
            chain: Array<out X509Certificate>?,
            authType: String?
        ) {}

        override fun checkServerTrusted(
            chain: Array<out X509Certificate>?,
            authType: String?
        ) {}

        override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
    })
    val sslContext = SSLContext.getInstance("SSL")

    init {
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory
        restClient = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier{ _, _ -> true }
            .build()
    }

    private val json = Json { ignoreUnknownKeys = true }

    override fun openCollection(name: String, area: Polygon
    ): CollectionInstance {
        var request = Request.Builder()
            .url("$baseUrl/collection")
            .post(Json.encodeToString(CollectionInstance(name,clientId,area)).toRequestBody("application/json".toMediaType()))
            .build()
        var response = restClient.newCall(request).execute()

        if(response.isSuccessful&&response.body !=null){
            var bodyString = response.body!!.string()
            response.close()
            return json.decodeFromString(bodyString)
        }else{
            response.close()
            throw OpenCollectionException("Could not create the collection")

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

    override fun setAreaDivision(collectionId: UUID, divisions: List<CollectionArea>) {
        var url = "$baseUrl/api/rest/collection/$collectionId"
        var request = Request.Builder()
            .url(url)
            .put(json.encodeToString(divisions).toRequestBody())
            .build()
        restClient.newCall(request).execute()
    }

    override fun assignCollectionArea(collectionId: UUID, area: CollectionArea, clientId: UUID?) {
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

    override fun openWebsocket() {
        webSocketClient.setSocketUrl("$baseUrl/ws")
        webSocketClient.setListener(socketListener)
        webSocketClient.connect()
    }

    override fun closeWebsocket() {
        webSocketClient.disconnect()
    }
}