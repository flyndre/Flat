package de.flyndre.flat.services

import android.util.Log
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ILocationService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.models.IncrementalTrackMessage
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.TrackCollection
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

class TrackingService(
    override val connectionService: IConnectionService,
    override val locationService: ILocationService,
    override val syncInterval: Long,
) : ITrackingService {
    override val localTrack: TrackCollection = TrackCollection(UUID.randomUUID())
    override val remoteTracks: MutableMap<UUID, TrackCollection> = mutableMapOf()
    override var isTracking: Boolean = false
    override val onLocalTrackUpdate: ArrayList<() -> Unit> = arrayListOf()
    override val onRemoteTrackUpdate: ArrayList<() -> Unit> = arrayListOf()

    init {
        locationService.addOnLocationUpdate {x-> addNewPosition(x) }
    }
    override fun startTracking() {
        localTrack.add(Track())
        locationService.startTracking()
        Log.d(this.toString(),"tracking started")
        isTracking = true
    }

    override fun stopTracking() {
        locationService.stopTracking()
        Log.d(this.toString(),"tracking stopped")
        isTracking = false
    }

    override fun addNewPosition(position: Position) {
        localTrack.last().add(position)
        onLocalTrackUpdate.forEach { x->x() }
    }

    override fun addIncrementalTrack(track: IncrementalTrackMessage) {
        if(!remoteTracks.containsKey(track.clientId)) {

            remoteTracks[track.clientId] = TrackCollection(track.clientId)
        }
        remoteTracks[track.clientId]?.addIncrementalTrack(track.trackId,track.track)
        onRemoteTrackUpdate.forEach { x->x() }
    }

    override fun addOnLocalTrackUpdate(callback: () -> Unit) {
        onLocalTrackUpdate.add(callback)
    }

    override fun addOnRemoteTrackUpdate(callback: () -> Unit) {
        onRemoteTrackUpdate.add(callback)
    }
}