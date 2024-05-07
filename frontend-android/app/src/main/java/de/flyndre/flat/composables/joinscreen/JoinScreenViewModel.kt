package de.flyndre.flat.composables.joinscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.flyndre.flat.composables.trackingscreen.TrackingScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.interfaces.IConnectionService
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
                val collcetionId = _joinLink.value.split('/').last()
                val answer = _connectionService.requestAccess(_joinName.value, UUID.fromString(collcetionId))
                if(answer.accepted){
                    _trackingScreenViewModel.collectionInstance= answer.collection!!
                    navigateToTrackingScreen()
                }else{
                    //handle deny
                }
            }catch (e: IllegalArgumentException){
                e.message?.let { Log.e(this.toString(), it) }
            }
        }
    }
}