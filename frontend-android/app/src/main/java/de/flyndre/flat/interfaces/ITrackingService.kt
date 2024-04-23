package de.flyndre.flat.interfaces

import de.flyndre.flat.models.TrackCollection
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

interface ITrackingService {
    val connectionService : IConnectionService
    val locationService : ILocationService
    val ownTrack : TrackCollection
    val otherTracks : MutableMap<UUID,TrackCollection>
    val syncInterval: Long
    var isTracking:Boolean
    val onLocalTrackUpdate : ArrayList<()->Unit>
    val onRemoteTrackUpdate : ArrayList<()->Unit>
    fun startTracking()
    fun stopTracking()
    fun addNewPosition(position: Position)
    fun addIncrementalTrack(track: TrackCollection)
    fun addOnLocalTrackUpdate(callback:()->Unit)
    fun addOnRemoteTrackUpdate(callback:()->Unit)
}