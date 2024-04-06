package de.flyndre.flat.composables.presetscreen

import androidx.lifecycle.ViewModel
import de.flyndre.flat.database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PresetScreenViewModel(presetId: Int?, db: AppDatabase) :ViewModel() {
    private val _presetName = MutableStateFlow("")
    val presetName: StateFlow<String> = _presetName.asStateFlow()
    fun updatePresetName(presetName: String){
        _presetName.value = presetName
    }

    private val _presetDescription = MutableStateFlow("")
    val presetDescription: StateFlow<String> = _presetDescription.asStateFlow()
    fun updatePresetDescription(presetDescription: String){
        _presetDescription.value = presetDescription
    }

    init{
        //check whether id is not null and fetch the preset from database
        if(presetId != null){
            val preset = db.presetDao().getPresetById(presetId = presetId)
            _presetName.value = preset.presetName
            _presetDescription.value = preset.presetDescription
        }
    }
}