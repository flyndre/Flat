package de.flyndre.flat.composables.trackingscreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import de.flyndre.flat.composables.trackingscreen.participantscreen.ParticipantScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.models.AccessResquestMessage
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.TrackCollection
import io.github.dellisd.spatialk.geojson.MultiPolygon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import qrcode.QRCode
import qrcode.render.QRCodeGraphics
import java.util.UUID


class TrackingScreenViewModel(
    db: AppDatabase,
    trackingService: ITrackingService,
    connectionService: IConnectionService,
    participantScreenViewModel: ParticipantScreenViewModel,
): ViewModel() {
    private var _db = db
    private val _participantScreenViewModel: ParticipantScreenViewModel = participantScreenViewModel
    private val _trackingService = trackingService
    private val _connectionService = connectionService
    private val joinBaseLink = "https://flat.buhss.de/join/"
    var collectionInstance: CollectionInstance = CollectionInstance("", UUID.randomUUID(),
        MultiPolygon()
    )
        set(value) {
            field = value
            _joinLink.value = joinBaseLink+value.id
            _qrCodeGraphics.value = QRCode.ofSquares().build(_joinLink.value).render()
        }

    private val _trackingEnabled = MutableStateFlow(false)
    val trackingEnabled = _trackingEnabled.asStateFlow()

    private val _trackList: MutableStateFlow<TrackCollection> = MutableStateFlow(TrackCollection())
    val trackList: StateFlow<TrackCollection> = _trackList.asStateFlow()

    private val _remoteTrackList: MutableStateFlow<Map<UUID,TrackCollection>> = MutableStateFlow(mapOf())
    val remoteTrackList: StateFlow<Map<UUID,TrackCollection>> = _remoteTrackList.asStateFlow()

    private val _participantsToJoin = MutableStateFlow(arrayListOf<AccessResquestMessage>())
    val participantsToJoin = _participantsToJoin.asStateFlow()

    private val _qrCodeGraphics = MutableStateFlow(QRCodeGraphics(1,1))
    val qrCodeGraphics: StateFlow<QRCodeGraphics> = _qrCodeGraphics.asStateFlow()

    private val _joinLink = MutableStateFlow(joinBaseLink+ collectionInstance.id)
    val joinLink : StateFlow<String> = _joinLink.asStateFlow()

    private val _cameraPosition = MutableStateFlow(CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F))
    val cameraPosition: StateFlow<CameraPosition> = _cameraPosition.asStateFlow()

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
        val tempList = arrayListOf<AccessResquestMessage>()
        tempList.addAll(_participantsToJoin.value)
        tempList.add(message)
        _participantsToJoin.value = tempList
    }

    fun updateParticipantScreenViewModel(){
        _participantScreenViewModel.initialValues(collectionInstance)
    }

    fun declineParticipantJoinDialog(message: AccessResquestMessage){
        viewModelScope.launch {
            collectionInstance = _connectionService.denyAccess(message)
            val tempList = arrayListOf<AccessResquestMessage>()
            tempList.addAll(_participantsToJoin.value)
            tempList.remove(message)
            _participantsToJoin.value = tempList
        }
    }

    fun acceptParticipantJoinDialog(message: AccessResquestMessage){
        viewModelScope.launch {
            collectionInstance = _connectionService.giveAccess(message)
            val tempList = arrayListOf<AccessResquestMessage>()
            tempList.addAll(_participantsToJoin.value)
            tempList.remove(message)
            _participantsToJoin.value = tempList
        }
    }

    fun leaveOrCloseCollection(isAdmin: Boolean){
        if(isAdmin){
            viewModelScope.launch {
                _connectionService.closeCollection(collectionInstance)
            }
        }else{
            viewModelScope.launch {
                _connectionService.leaveCollection(collectionInstance)
            }
        }
    }

    fun shareJoinLink(){


    }
}