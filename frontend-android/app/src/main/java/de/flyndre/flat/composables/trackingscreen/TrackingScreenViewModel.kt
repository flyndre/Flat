package de.flyndre.flat.composables.trackingscreen

import androidx.lifecycle.ViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.TrackCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TrackingScreenViewModel(
    db: AppDatabase,
    trackingService: ITrackingService,
): ViewModel() {
    private var _db = db
    private val _trackingService = trackingService
    lateinit var collectionInstance: CollectionInstance

    private val _trackingEnabled = MutableStateFlow(false)
    val trackingEnabled = _trackingEnabled.asStateFlow()

    private val _trackList: MutableStateFlow<TrackCollection> = MutableStateFlow(TrackCollection())
    val trackList: StateFlow<TrackCollection> = _trackList.asStateFlow()

    private val _remoteTrackList: MutableStateFlow<Map<UUID,TrackCollection>> = MutableStateFlow(mapOf())
    val remoteTrackList: StateFlow<Map<UUID,TrackCollection>> = _remoteTrackList.asStateFlow()


    init {
        trackingService.addOnLocalTrackUpdate{ onLocalTrackUpdate() }
        trackingService.addOnRemoteTrackUpdate { onRemoteTrackUpdate() }
    }

    fun toggleTracking(){
        if(_trackingEnabled.value){
            _trackingService.stopTracking()
            _trackingEnabled.value = false
        }else{
            _trackingService.startTracking()
            _trackingEnabled.value = true
        }
    }

    private fun onLocalTrackUpdate(){
        _trackList.value = _trackingService.localTrack
    }
    private fun onRemoteTrackUpdate(){
        _remoteTrackList.value = _trackingService.remoteTracks
    }

}