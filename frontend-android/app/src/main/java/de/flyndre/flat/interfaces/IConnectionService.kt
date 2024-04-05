package de.flyndre.flat.interfaces

import de.flyndre.flat.models.AccessResquestMessage
import de.flyndre.flat.models.CollectionArea
import de.flyndre.flat.models.CollectionClosedMessage
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.IncrementalTrackMessage
import de.flyndre.flat.models.RequestAccessResult
import de.flyndre.flat.models.Track
import io.github.dellisd.spatialk.geojson.Polygon
import java.util.UUID

interface IConnectionService {

    var onAccessResquest: ((AccessResquestMessage) -> Unit)?
    var onCollectionClosed: ((CollectionClosedMessage) -> Unit)?
    var onTrackUpdate: ((IncrementalTrackMessage) -> Unit)?

    fun openCollection(name: String,area: Polygon ,divisions: List<CollectionArea>) : CollectionInstance
    fun closeCollection(collection: CollectionInstance)
    fun setAreaDivision(divisions: List<CollectionArea>)
    fun assignCollectionArea(area: CollectionArea, clientId: UUID? = null)
    fun requestAccess(username: String, collectionId: UUID) : RequestAccessResult
    fun giveAccess(request: AccessResquestMessage)
    fun denyAccess(request: AccessResquestMessage)
    fun leaveCollection(collection: CollectionInstance)
    fun sendTrackUpdate(track: Track)

}