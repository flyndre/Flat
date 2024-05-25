package de.flyndre.flat.composables.joinscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.flyndre.flat.composables.trackingscreen.TrackingScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ISettingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.UUID

class JoinScreenViewModel(db: AppDatabase, settingService: ISettingService, trackingScreenViewModel: TrackingScreenViewModel, connectionService: IConnectionService): ViewModel() {
    //appdatabase
    private val _db = db
    private val _trackingScreenViewModel = trackingScreenViewModel
    //connection service
    private val _connectionService = connectionService
    private val _settingService = settingService
    //join link
    private val _joinLink = MutableStateFlow("")
    val joinLink: StateFlow<String> = _joinLink.asStateFlow()

    fun updateJoinLink(joinLink: String){
        _joinLink.value = joinLink
    }

    //join name
    private val _joinName = MutableStateFlow(_settingService.getDefaultUserName())
    val joinName: StateFlow<String> = _joinName.asStateFlow()

    fun updateJoinName(joinName: String) {
        _joinName.value = joinName
        _settingService.setDefaultUserName(joinName)
    }

    //last collections
    private val _lastCollections = MutableStateFlow(_settingService.getResentJoinLinks())
    val lastCollections = _lastCollections.asStateFlow()
    fun updateLastCollections(lastCollection:String){
        _lastCollections.value = _settingService.addResentJoinLink(lastCollection)
    }

    //connection error handling
    private val _showConnectionError = MutableStateFlow(false)
    val showConnectionError = _showConnectionError.asStateFlow()

    fun hideConnectionError(){
        _showConnectionError.value = false
    }

    fun join(navigateToTrackingScreen: ()->Unit){
        viewModelScope.launch {
            try {
                updateLastCollections(joinLink.value)
                val collcetionId = _joinLink.value.split('/').last()
                val answer = _connectionService.requestAccess(_joinName.value, UUID.fromString(collcetionId))
                if(answer.accepted){
                    _trackingScreenViewModel.collectionInstance= answer.collection!!
                    _connectionService.openWebsocket(answer.collection.id!!)

                    navigateToTrackingScreen()
                }
            }catch (e: IllegalArgumentException){
                e.message?.let { Log.e(this.toString(), it) }
                _showConnectionError.value = true
            }
        }
    }
}
class JoinScreenViewModelFactory(
    val db: AppDatabase,
    val settingService: ISettingService,
    val trackingScreenViewModel: TrackingScreenViewModel,
    val connectionService: IConnectionService) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return JoinScreenViewModel(db,settingService,trackingScreenViewModel,connectionService) as T
    }
}