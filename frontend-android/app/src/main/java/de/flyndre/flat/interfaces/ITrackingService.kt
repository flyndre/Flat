package de.flyndre.flat.interfaces

import com.google.android.gms.maps.model.LatLng
import de.flyndre.flat.models.IncrementalTrackMessage
import de.flyndre.flat.models.TrackCollection
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

interface ITrackingService {
    val connectionService : IConnectionService
    val locationService : ILocationService
    val localTrack : TrackCollection
    val remoteTracks : MutableMap<UUID,TrackCollection>
    val syncInterval: Long
    var isTracking:Boolean
    val onLocalTrackUpdate : ArrayList<()->Unit>
    val onRemoteTrackUpdate : ArrayList<()->Unit>
    fun startTracking()
    fun stopTracking()
    fun addNewPosition(position: Position)
    fun addIncrementalTrack(track: IncrementalTrackMessage)
    fun addOnLocalTrackUpdate(callback:()->Unit)
    fun addOnRemoteTrackUpdate(callback:()->Unit)
    suspend fun getCurrentPosition():LatLng
}