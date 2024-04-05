package de.flyndre.flat.services

import android.util.Log
import de.flyndre.flat.interfaces.ILocationService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.models.Track
import io.github.dellisd.spatialk.geojson.Position
import java.util.Dictionary
import java.util.UUID

class TrackingService: ITrackingService {
    override var locationService: ILocationService = RandomLocationService(this,100)
    override var ownTrack: Track = Track(UUID.randomUUID())
    override var otherTracks: MutableMap<UUID, Track> = mutableMapOf()
    override fun startTracking() {
        locationService.startTracking()
    }

    override fun stopTracking() {
        locationService.stopTracking()
    }

    override fun addNewPosition(position: Position) {
        ownTrack.add(position)
        var s = ownTrack.toLineString().toString()
        s = ""
    }

    override fun addIncrementalTrack(track: Track) {
        if(otherTracks.containsKey(track.trackId)) {
            otherTracks[track.trackId]!!.addAll(track)
        }else{
            otherTracks.put(track.trackId,track)
        }
    }
}