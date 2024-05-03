package de.flyndre.flat.composables.trackingscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.flyndre.flat.composables.trackingscreen.participantscreen.ParticipantScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.models.AccessResquestMessage
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.TrackCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TrackingScreenViewModel(
    db: AppDatabase,
    trackingService: ITrackingService,
    connectionService: IConnectionService,
    participantScreenViewModel: ParticipantScreenViewModel
): ViewModel() {
    private var _db = db
    private val _participantScreenViewModel: ParticipantScreenViewModel = participantScreenViewModel
    private val _trackingService = trackingService
    private val _connectionService = connectionService
    lateinit var collectionInstance: CollectionInstance
    lateinit var accessResquestMessage: AccessResquestMessage

    private val _trackingEnabled = MutableStateFlow(false)
    val trackingEnabled = _trackingEnabled.asStateFlow()

    private val _trackList: MutableStateFlow<TrackCollection> = MutableStateFlow(TrackCollection())
    val trackList: StateFlow<TrackCollection> = _trackList.asStateFlow()

    private val _remoteTrackList: MutableStateFlow<Map<UUID,TrackCollection>> = MutableStateFlow(mapOf())
    val remoteTrackList: StateFlow<Map<UUID,TrackCollection>> = _remoteTrackList.asStateFlow()

    private val _showParticipantJoinDialog = MutableStateFlow(false)
    val showParticipantJoinDialog = _showParticipantJoinDialog.asStateFlow()

    init {
        trackingService.addOnLocalTrackUpdate{ onLocalTrackUpdate() }
        trackingService.addOnRemoteTrackUpdate { onRemoteTrackUpdate() }
        connectionService.addOnAccessRequest { onAccessRequestMessage(it) }
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

    private fun onAccessRequestMessage(message: AccessResquestMessage){
        accessResquestMessage = message
        _showParticipantJoinDialog.value = true
    }

    fun updateParticipantScreenViewModel(){
        _participantScreenViewModel.initialValues(collectionInstance)
    }

    fun declineParticipantJoinDialog(){
        viewModelScope.launch {
            collectionInstance = _connectionService.denyAccess(accessResquestMessage)
            _showParticipantJoinDialog.value = false
        }
    }

    fun accpetParticipantJoinDialog(){
        viewModelScope.launch {
            collectionInstance = _connectionService.giveAccess(accessResquestMessage)
            _showParticipantJoinDialog.value = false
        }
    }
}