package de.flyndre.flat.composables.creategroupscreen

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import de.flyndre.flat.composables.presetscreen.PresetScreenViewModel
import de.flyndre.flat.composables.trackingscreen.participantscreen.ParticipantScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.database.entities.Preset
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.interfaces.ITrackingService
import de.flyndre.flat.services.ConnectionService
import de.flyndre.flat.services.TrackingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class CreateGroupScreenViewModel(db: AppDatabase, presetScreenViewModel: PresetScreenViewModel) :
    ViewModel() {
    private val _db = db
    private val _presetScreenViewModel = presetScreenViewModel
    private val _presets = db.presetDao().getAll()
    val presets = _presets.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    private var _idToBeDeleted: Long = 0

    fun navigateToPreset(id: Long, onNavigateToPresetScreen: () -> Unit) {
        if (id == 0.toLong()) {
            _presetScreenViewModel.clearPresetValues()
            onNavigateToPresetScreen()
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val preset = _db.presetDao().getPresetById(id)
                _presetScreenViewModel.setPresetValues(
                    id,
                    preset.presetName,
                    preset.presetDescription,
                    preset.presetCollectionAreas,
                    preset.presetCameraPosition
                )
                viewModelScope.launch(Dispatchers.Main) {
                    onNavigateToPresetScreen()
                }
            }
        }
    }

    fun deleteSetPreset() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_idToBeDeleted != 0.toLong()) {
                _db.presetDao().deletePresetById(_idToBeDeleted)
            }
        }
    }

    fun setIdToBeDeleted(id: Long) {
        _idToBeDeleted = id
    }
}

class CreateGroupScreenViewModelFactory(
    val db: AppDatabase,
    val presetScreenViewModel: PresetScreenViewModel,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateGroupScreenViewModel(db, presetScreenViewModel) as T
    }
}