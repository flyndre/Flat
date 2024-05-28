package de.flyndre.flat.services

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ILocationService
import de.flyndre.flat.interfaces.ISettingService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.models.IncrementalTrackMessage
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.TrackCollection
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.UUID
import java.util.concurrent.Executors

class TrackingService(
    override val connectionService: IConnectionService,
    override val locationService: ILocationService,
    override val syncInterval: Long,
    val settingService: ISettingService,
) : ITrackingService {
    override val localTrack: TrackCollection = TrackCollection(UUID.randomUUID())
    override val remoteTracks: MutableMap<UUID, TrackCollection> = mutableMapOf()
    override var isTracking: Boolean = false
    override val onLocalTrackUpdate: ArrayList<() -> Unit> = arrayListOf()
    override val onRemoteTrackUpdate: ArrayList<() -> Unit> = arrayListOf()
    private val sendUpdateLock = Mutex()
    val messages = arrayListOf<IncrementalTrackMessage>()

    init {
        locationService.addOnLocationUpdate {addNewPosition(it) }
        connectionService.addOnTrackUpdate { addIncrementalTrack(it) }
    }
    override fun startTracking() {
        val newTrack = Track()
        localTrack.tracks.add(newTrack)
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
        localTrack.tracks.last().positions.add(position)
        onLocalTrackUpdate.forEach { x->x() }
    }

    override fun addIncrementalTrack(track: IncrementalTrackMessage) {
        try{
            if(messages.contains(track)){
                Log.d(this.toString(),"Double message +${track}")
                return
            }
            messages.add(track)
            if(track.clientId!=settingService.getClientId()){
                if(!remoteTracks.containsKey(track.clientId)) {

                    remoteTracks[track.clientId] = TrackCollection(track.clientId)
                }
                remoteTracks[track.clientId]?.addIncrementalTrack(track.trackId,track.track)
                onRemoteTrackUpdate.forEach { x->x() }
            }
        } catch (e:Exception){
            Log.e(this.toString(),e.message.toString())
        }
    }

    override fun addOnLocalTrackUpdate(callback: () -> Unit) {
        onLocalTrackUpdate.add(callback)
    }

    override fun addOnRemoteTrackUpdate(callback: () -> Unit) {
        onRemoteTrackUpdate.add(callback)
    }

    override suspend fun getCurrentPosition(): LatLng {
        return locationService.getCurrentPosition()
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
            val newestPointIndex = track.positions.lastIndex
            if(newestPointIndex <= lastPointIndex+1){
                return lastPointIndex
            }
            val incrementalTrack = Track(track.trackId)
            incrementalTrack.positions.addAll(track.positions.subList(lastPointIndex,newestPointIndex))
            connectionService.sendTrackUpdate(incrementalTrack)
            return newestPointIndex
        }catch (e:Exception){
            e.message?.let { Log.e(this.toString(), it) }
            return  lastPointIndex
        }
        finally {
            sendUpdateLock.unlock()
        }
    }
}