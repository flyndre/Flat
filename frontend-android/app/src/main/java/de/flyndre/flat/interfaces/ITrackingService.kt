package de.flyndre.flat.interfaces

import de.flyndre.flat.models.Track
import de.flyndre.flat.models.TrackCollection
import io.github.dellisd.spatialk.geojson.Position
import java.util.Dictionary
import java.util.UUID

interface ITrackingService {

    var locationService : ILocationService
    var ownTrack : TrackCollection
    var otherTracks : MutableMap<UUID,TrackCollection>
    fun startTracking()
    fun stopTracking()
    fun addNewPosition(position: Position)
    fun addIncrementalTrack(track: TrackCollection)
}