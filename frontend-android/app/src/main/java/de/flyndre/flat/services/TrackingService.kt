package de.flyndre.flat.services

import android.util.Log
import de.flyndre.flat.interfaces.ILocationService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.TrackCollection
import io.github.dellisd.spatialk.geojson.Position
import java.util.Dictionary
import java.util.UUID

class TrackingService: ITrackingService {
    override var locationService: ILocationService = RandomLocationService(this,100)
    override var ownTrack: TrackCollection = TrackCollection(UUID.randomUUID())
    override var otherTracks: MutableMap<UUID, TrackCollection> = mutableMapOf()
    override fun startTracking() {
        ownTrack.add(Track())
        locationService.startTracking()
        Log.d(this.toString(),"tracking started")
    }

    override fun stopTracking() {
        locationService.stopTracking()
        Log.d(this.toString(),"tracking stopped")
    }

    override fun addNewPosition(position: Position) {
        ownTrack.last().add(position)
        var s = ownTrack.toMultiLineString().toString()
        s = ""
    }

    override fun addIncrementalTrack(track: TrackCollection) {
        if(otherTracks.containsKey(track.id)) {
            otherTracks[track.id]!!.addAll(track)
        }else{
            otherTracks[track.id] = track
        }
    }
}