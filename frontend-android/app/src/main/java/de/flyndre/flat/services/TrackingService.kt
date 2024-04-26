package de.flyndre.flat.services

import android.util.Log
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ILocationService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.models.IncrementalTrackMessage
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.TrackCollection
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.Executors

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
    private val sendUpdateLock = Mutex()

    init {
        locationService.addOnLocationUpdate {x-> addNewPosition(x) }
    }
    override fun startTracking() {
        var newTrack = Track()
        localTrack.add(newTrack)
        locationService.startTracking()
        Log.d(this.toString(),"tracking started")
        isTracking = true
        CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher()).launch {
            startSendTrackUpdate(newTrack)
        }
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

    suspend fun startSendTrackUpdate(track: Track){
        var lastPointIndex = 0
        while (isTracking){
            lastPointIndex= sendTrackUpdate(track,lastPointIndex)
            delay(syncInterval)
        }
        sendTrackUpdate(track,lastPointIndex,true)

    }

    private suspend fun sendTrackUpdate(track: Track, lastPointIndex: Int, ignoreRunning: Boolean = false):Int{
        if(sendUpdateLock.isLocked&&!ignoreRunning){
            return lastPointIndex
        }
        sendUpdateLock.lock()
        try {
            val newstPointIndex = track.lastIndex
            if(newstPointIndex<=lastPointIndex){
                return lastPointIndex
            }
            val incrementalTrack = Track(track.trackId)
            incrementalTrack.addAll(track.subList(lastPointIndex,newstPointIndex))
            connectionService.sendTrackUpdate(incrementalTrack)
            return newstPointIndex
        }catch (e:Exception){
            e.message?.let { Log.e(this.toString(), it) }
            return  lastPointIndex
        }
        finally {
            sendUpdateLock.unlock()
        }
    }
}