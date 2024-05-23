package de.flyndre.flat.composables.creategroupscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.flyndre.flat.composables.presetscreen.PresetScreenViewModel
import de.flyndre.flat.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
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