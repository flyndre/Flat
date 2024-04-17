package de.flyndre.flat.interfaces

import de.flyndre.flat.models.AccessResquestMessage
import de.flyndre.flat.models.CollectionArea
import de.flyndre.flat.models.CollectionClosedMessage
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.CollectionUpdateMessage
import de.flyndre.flat.models.IncrementalTrackMessage
import de.flyndre.flat.models.RequestAccessResult
import de.flyndre.flat.models.Track
import io.github.dellisd.spatialk.geojson.MultiPolygon
import java.util.UUID

/**
 * Interface for all backend traffic. Provides all necessary functions to communicate with the flat backend.
 */
interface IConnectionService {

    val onAccessRequest : ArrayList<(AccessResquestMessage) -> Unit>
    val onCollectionClosed: ArrayList<(CollectionClosedMessage) -> Unit>
    val onTrackUpdate: ArrayList<(IncrementalTrackMessage) -> Unit>
    val onCollectionUpdate: ArrayList<(CollectionUpdateMessage)->Unit>

    /**
     * opens a collection to the public
     * @param name name of the Collection
     * @param area the base area in which the collection will be
     * @return a CollectionInstance if the request was successful
     * @throws de.flyndre.flat.exceptions.RequestFailedException if request was not successful
     */
    suspend fun openCollection(name: String, area: MultiPolygon) : CollectionInstance

    /**
     * Call to the backend to close a collection and remove it from the public
     * @param collection the collection to close
     */
    suspend fun closeCollection(collection: CollectionInstance)

    /**
     * sets or updates the divisions of a collection area.
     * @param collectionId id of an existing public collection
     * @param divisions list of divisions new ones will be created, existing ones updated, missing ones deleted
     */
    suspend fun setAreaDivision(collectionId: UUID, divisions: List<CollectionArea>)

    /**
     * assigns a user to a division of a collection area.
     * @param collectionId id of an public collection
     * @param area division to assign the user to
     * @param clientId the client to assign to. If null the assigned user will be unassigned
     */
    suspend fun assignCollectionArea(collectionId: UUID,area: CollectionArea, clientId: UUID? = null)

    /**
     * Sends a request to access a collection
     * @param username a username to display to other users
     * @param collectionId the id of the collection to request
     * @return the result of the request inside a RequestAccessResult object.
     */
    suspend fun requestAccess(username: String, collectionId: UUID) : RequestAccessResult

    /**
     * Grants a user access to an owned collection. Results in a positive RequestAccessResult for the requested user.
     * @param request the access request from the user
     */
    suspend fun giveAccess(request: AccessResquestMessage)

    /**
     * Denys a user access to an owned collection. Results in a negative RequestAccessResult for the requested user.
     * @param request the access request from the user
     */
    suspend fun denyAccess(request: AccessResquestMessage)

    /**
     * Notify the backend and other users, that this user is leaving the collection
     * @param collection the collection to leave
     */
    suspend fun leaveCollection(collection: CollectionInstance)

    /**
     * Send an incremental track update to the backend and all other users.
     * @param track the incremental track
     */
    suspend fun sendTrackUpdate(track: Track)

    /**
     * Opens the websocket to the backend to receive messages from it.
     * @param onAccessRequest a callback to be called when an access request is made to a owned collection.
     * @param onCollectionClosed a callback to be notified if a collection is closed from the owner.
     * @param onTrackUpdate a callback to be called when a track update is received from an other user.
     * @param onCollectionUpdate
     */
    suspend fun openWebsocket(onAccessRequest: ((AccessResquestMessage) -> Unit)? = null,
                              onCollectionClosed: ((CollectionClosedMessage) -> Unit)? = null,
                              onTrackUpdate: ((IncrementalTrackMessage) -> Unit)? = null,
                              onCollectionUpdate: ((CollectionUpdateMessage)->Unit)?=null)

    /**
     * closes the websocket
     */
    suspend fun closeWebsocket()

    /**
     * Adds a callback to be called when an access request is made to a owned collection.
     */
    fun addOnAccessRequest(callback :(AccessResquestMessage) -> Unit)

    /**
     * Adds a callback to be called if a collection is closed from the owner.
     */
    fun addOnCollectionClosed(callback: (CollectionClosedMessage) -> Unit)

    /**
     * Adds a callback when a track update is received from an other user.
     */
    fun addOnTrackUpdate(callback: (IncrementalTrackMessage) -> Unit)

    /**
     * Adds a callback to be called when an collection update is received
     */
    fun addOnCollectionUpdate(callback: (CollectionUpdateMessage)->Unit)


}