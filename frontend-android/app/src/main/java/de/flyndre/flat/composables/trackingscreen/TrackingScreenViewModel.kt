package de.flyndre.flat.composables.trackingscreen

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
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
    private val _startStopButtonText: MutableStateFlow<String> = MutableStateFlow("Start tracking")
    val startStopButtonText: StateFlow<String> = _startStopButtonText.asStateFlow()
    private val _trackList: MutableStateFlow<Map<UUID,TrackCollection>> = MutableStateFlow(mapOf())
    val trackList: StateFlow<Map<UUID,TrackCollection>> = _trackList.asStateFlow()

    init {
        trackingService.addOnTrackUpdate{->onTrackUpdate()}
    }

    fun toggleTracking(){
        if(_trackingService.isTracking){
            _trackingService.stopTracking()
            _startStopButtonText.value= "Start tracking"
        }else{
            _trackingService.startTracking()
            _startStopButtonText.value = "Stop tracking"
        }
    }

    private fun onTrackUpdate(){
        _trackList.value = mapOf(Pair(UUID.randomUUID(),_trackingService.ownTrack))
    }

}