package de.flyndre.flat.services

import android.util.Log
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ILocationService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.TrackCollection
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

class TrackingService(
    override val connectionService: IConnectionService,
    override val locationService: ILocationService,
    override val syncInterval: Long,
) : ITrackingService {
    override val ownTrack: TrackCollection = TrackCollection(UUID.randomUUID())
    override val otherTracks: MutableMap<UUID, TrackCollection> = mutableMapOf()
    override var isTracking: Boolean = false
    override val onTrackUpdate: ArrayList<() -> Unit> = arrayListOf()

    init {
        locationService.addOnLocationUpdate {x-> addNewPosition(x) }
    }
    override fun startTracking() {
        ownTrack.add(Track())
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
        ownTrack.last().add(position)
        var s = ownTrack.toMultiLineString().toString()
        s = ""
        onTrackUpdate.forEach { x->x() }
    }

    override fun addIncrementalTrack(track: TrackCollection) {
        if(otherTracks.containsKey(track.id)) {
            otherTracks[track.id]!!.addAll(track)
        }else{
            otherTracks[track.id] = track
        }
    }

    override fun addOnTrackUpdate(callback: () -> Unit) {
        onTrackUpdate.add(callback)
    }
}