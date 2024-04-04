package de.flyndre.flat.services

import android.util.Log
import de.flyndre.flat.WebSocketClient
import de.flyndre.flat.models.IncrementalTrackMessage
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.WebSocketMessage
import de.flyndre.flat.models.WebSocketMessageType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WebSocketService {
    var client: WebSocketClient
    private val socketListener = object : WebSocketClient.SocketListener {
        override fun onMessage(message: String) {
            var obj = Json.decodeFromString<WebSocketMessage>(message)
            when(obj.type){
                WebSocketMessageType.IncrementalTrack -> addIncrementalTrack( obj as IncrementalTrackMessage)
            }
        }
    }
    init {
        client = WebSocketClient.getInstance()
        client.setSocketUrl("https://10.0.2.2:44380/ws")
        client.setListener(socketListener)
        client.connect()
    }

    fun addIncrementalTrack(track: IncrementalTrackMessage){

    }

    fun sendTrackUpdate(track: Track){
        var message = Json.encodeToString(IncrementalTrackMessage(track.trackId.toString(),track.toLineString()))
        client.sendMessage(message)
        var s = track.toLineString().toString()
    }
}