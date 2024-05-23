package de.flyndre.flat.composables.trackingscreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import de.flyndre.flat.composables.trackingscreen.assignmentscreen.AssignmentScreenViewModel
import de.flyndre.flat.composables.trackingscreen.participantscreen.ParticipantScreenViewModel
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ISettingService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.models.AccessResquestMessage
import de.flyndre.flat.models.CollectionArea
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.CollectionUpdateMessage
import de.flyndre.flat.models.LeavingUserMessage
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.TrackCollection
import io.github.dellisd.spatialk.geojson.MultiPolygon
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import qrcode.QRCode
import qrcode.render.QRCodeGraphics
import java.util.UUID


class TrackingScreenViewModel(
    trackingService: ITrackingService,
    connectionService: IConnectionService,
    assignmentScreenViewModel: AssignmentScreenViewModel,
    participantScreenViewModel: ParticipantScreenViewModel,
    settingService: ISettingService
): ViewModel() {
    private val _assignmentScreenViewModel: AssignmentScreenViewModel = assignmentScreenViewModel
    private val _participantScreenViewModel: ParticipantScreenViewModel = participantScreenViewModel
    private val _trackingService = trackingService
    private val _connectionService = connectionService
    private val _settingService = settingService
    private val joinBaseLink = "https://flat.buhss.de/join/"
    private var lastCenteredOwnDivision: CollectionArea? = null

    var collectionInstance: CollectionInstance = CollectionInstance("", UUID.randomUUID(), MultiPolygon())
        set(value) {
            field = value
            _divisionList.value = value.collectionDivision
            _joinLink.value = joinBaseLink+value.id
            _qrCodeGraphics.value = QRCode.ofSquares().build(_joinLink.value).render()
        }

    private val _trackingEnabled = MutableStateFlow(false)
    val trackingEnabled = _trackingEnabled.asStateFlow()

    private val _divisionList = MutableStateFlow(arrayListOf<CollectionArea>())
    val divisionList = _divisionList.asStateFlow()

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

    private val _clientId = MutableStateFlow(_settingService.getClientId().toString())
    val clientId : StateFlow<String> = _clientId.asStateFlow()

    private val _participantsLeaved = MutableStateFlow(arrayListOf<LeavingUserMessage>())
    val participantsLeaved = _participantsLeaved.asStateFlow()

    private val _showCollectionClosedDialog = MutableStateFlow(false)
    val showCollectionClosedDialog = _showCollectionClosedDialog.asStateFlow()

    private val _showParticipantKickedDialog = MutableStateFlow(false)
    val showParticipantKickedDialog = _showParticipantKickedDialog.asStateFlow()

    init {
        trackingService.addOnLocalTrackUpdate{ onLocalTrackUpdate() }
        trackingService.addOnRemoteTrackUpdate { onRemoteTrackUpdate() }
        connectionService.addOnAccessRequest { onAccessRequestMessage(it) }
        connectionService.addOnUserLeaved { onUserLeavedCollection(it) }
        connectionService.addOnCollectionUpdate { onCollectionUpdate(it) }
        connectionService.addOnCollectionClosed { onCollectionClosed() }
        connectionService.addOnUserKicked { onParticipantKicked() }

        //add initial point for own location
        viewModelScope.launch(Dispatchers.Default) {
            val pos = _trackingService.getCurrentPosition()
            val track = Track(positions = arrayListOf(Position(pos.longitude, pos.latitude)))
            _trackList.value = TrackCollection(clientId = _trackList.value.clientId, arrayListOf(track))
        }
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

    private fun onParticipantKicked(){
        _showParticipantKickedDialog.value = true
    }

    private fun onCollectionClosed(){
        _showCollectionClosedDialog.value = true
    }

    private fun onLocalTrackUpdate(){
        val s = _trackingService.localTrack.deepCopy()
        _trackList.value = s
    }
    private fun onRemoteTrackUpdate(){
        val newMap : MutableMap<UUID,TrackCollection> = mutableMapOf()
        _trackingService.remoteTracks.forEach{
            newMap[UUID.fromString(it.key.toString())] = it.value.deepCopy()
        }
        _remoteTrackList.value = newMap
    }

    private fun onUserLeavedCollection(leavingUserMessage: LeavingUserMessage){
        _participantsLeaved.value.add(leavingUserMessage)
    }

    private fun onCollectionUpdate(collectionUpdateMessage: CollectionUpdateMessage){
        collectionInstance = collectionUpdateMessage.collection
    }

    private fun onAccessRequestMessage(message: AccessResquestMessage){
        val tempList = arrayListOf<AccessResquestMessage>()
        tempList.addAll(_participantsToJoin.value)
        tempList.add(message)
        _participantsToJoin.value = tempList
    }

    fun removeFirstLeavedparticipant(){
        _participantsLeaved.value.removeFirst()
    }

    fun updateAssignmentScreenViewModel(){
        _assignmentScreenViewModel.initialValues(collectionInstance)
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

    fun centerOnPosition(cameraPositionState: CameraPositionState){
        var currentPosition :LatLng
        viewModelScope.launch(Dispatchers.Default){
            currentPosition = _trackingService.getCurrentPosition()
            viewModelScope.launch(Dispatchers.Main) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(currentPosition, 18F))
            }
        }
    }

    fun centerOnOwnArea(cameraPositionState: CameraPositionState){
        //get area to center
        var division: CollectionArea? = null
        for(div in collectionInstance.collectionDivision){
            if(div.clientId != null){
                if(div.clientId!!.equals(_settingService.getClientId())){
                    division = div
                    if(lastCenteredOwnDivision != null){
                        if(!div.id.equals(lastCenteredOwnDivision!!.id)){
                            break
                        }
                    }
                }
            }
        }

        if(division == null && lastCenteredOwnDivision != null){
            division = lastCenteredOwnDivision
        }

        if(division != null){
            //center on selected area
            val builder = LatLngBounds.builder()

            for(position in division.area.coordinates[0]){
                builder.include(LatLng(position.latitude, position.longitude))
            }

            viewModelScope.launch(Dispatchers.Main) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(builder.build(), 10))
            }

            lastCenteredOwnDivision = division
        }
    }

    fun isThisUserAdmin(): Boolean{
        return collectionInstance.clientId.equals(_settingService.getClientId())
    }
}

class TrackingScreenViewModelFactory(
    val trackingService: ITrackingService,
    val connectionService: IConnectionService,
    val assignmentScreenViewModel: AssignmentScreenViewModel,
    val participantScreenViewModel: ParticipantScreenViewModel,
    val settingService: ISettingService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TrackingScreenViewModel(
            trackingService,
            connectionService,
            assignmentScreenViewModel,
            participantScreenViewModel,
            settingService
        ) as T
    }
}