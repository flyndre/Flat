package de.flyndre.flat.interfaces

import de.flyndre.flat.models.Track
import io.github.dellisd.spatialk.geojson.Position
import java.util.Dictionary
import java.util.UUID

interface ITrackingService {

    var locationService : ILocationService
    var ownTrack : Track
    var otherTracks : MutableMap<UUID,Track>
    fun startTracking()
    fun stopTracking()
    fun addNewPosition(position: Position)
    fun addIncrementalTrack(track: Track)
}