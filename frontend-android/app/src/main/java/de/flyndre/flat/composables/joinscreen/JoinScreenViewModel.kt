package de.flyndre.flat.composables.joinscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.flyndre.flat.composables.trackingscreen.TrackingScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.services.ConnectionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.UUID

class JoinScreenViewModel(db: AppDatabase,trackingScreenViewModel: TrackingScreenViewModel, connectionService: IConnectionService): ViewModel() {
    //appdatabase
    private val _db = db
    private val _trackingScreenViewModel = trackingScreenViewModel
    //connection service
    private val _connectionService = connectionService
    //join link
    private val _joinLink = MutableStateFlow("")
    val joinLink: StateFlow<String> = _joinLink.asStateFlow()

    fun updateJoinLink(joinLink: String){
        _joinLink.value = joinLink
    }

    //join name
    private val _joinName = MutableStateFlow("")
    val joinName: StateFlow<String> = _joinName.asStateFlow()

    fun updateJoinName(joinName: String){
        _joinName.value = joinName
    }

    fun join(navigateToTrackingScreen: ()->Unit){
        viewModelScope.launch {
            try {
                val answer = _connectionService.requestAccess(_joinName.value, UUID.fromString(_joinLink.value))
                if(answer.accepted){
                    _trackingScreenViewModel.collectionInstance= answer.collection!!
                    navigateToTrackingScreen()
                }else{
                    //handle deny
                }
            }catch (e: IllegalArgumentException){
                //
            }
        }
    }
}