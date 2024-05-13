package de.flyndre.flat.composables.joinscreen

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.flyndre.flat.composables.creategroupscreen.CreateGroupScreenViewModel
import de.flyndre.flat.composables.trackingscreen.TrackingScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.interfaces.IConnectionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.UUID
import java.util.prefs.Preferences

class JoinScreenViewModel(db: AppDatabase,preferences:SharedPreferences,trackingScreenViewModel: TrackingScreenViewModel, connectionService: IConnectionService): ViewModel() {
    //appdatabase
    private val _db = db
    private val _trackingScreenViewModel = trackingScreenViewModel
    //connection service
    private val _connectionService = connectionService
    private val _preferences = preferences
    private val _usernameKey = "USERNAME"
    private val _lastCollectionsKey = "LASTCOLLECTIONS"
    //join link
    private val _joinLink = MutableStateFlow("")
    val joinLink: StateFlow<String> = _joinLink.asStateFlow()

    fun updateJoinLink(joinLink: String){
        _joinLink.value = joinLink
    }

    //join name
    private val _joinName = MutableStateFlow(preferences.getString(_usernameKey,"").toString())
    val joinName: StateFlow<String> = _joinName.asStateFlow()

    fun updateJoinName(joinName: String){
        _joinName.value = joinName
        _preferences.edit{
            putString(_usernameKey,joinName)
        }
    }
    private val _lastCollections = MutableStateFlow(preferences.getStringSet(_lastCollectionsKey,
        setOf())!!)
    val lastCollections = _lastCollections.asStateFlow()
    fun updateLastCollections(lastCollection:String){
        var newSet = _lastCollections.value
        if(newSet.size>=5){
            newSet = newSet-newSet.first()
        }
        newSet = newSet+lastCollection
        _lastCollections.value = newSet
        _preferences.edit {
            putStringSet(_lastCollectionsKey, newSet)
        }
    }

    fun join(navigateToTrackingScreen: ()->Unit){
        viewModelScope.launch {
            try {
                val collcetionId = _joinLink.value.split('/').last()
                val answer = _connectionService.requestAccess(_joinName.value, UUID.fromString(collcetionId))
                if(answer.accepted){
                    _trackingScreenViewModel.collectionInstance= answer.collection!!
                    _connectionService.openWebsocket(answer.collection.id!!)
                    updateLastCollections(joinLink.value)
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
class JoinScreenViewModelFactory(
    val db: AppDatabase,
    val preferences: SharedPreferences,
    val trackingScreenViewModel: TrackingScreenViewModel,
    val connectionService: IConnectionService) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return JoinScreenViewModel(db,preferences,trackingScreenViewModel,connectionService) as T
    }
}