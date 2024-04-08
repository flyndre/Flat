package de.flyndre.flat.composables.presetscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.database.entities.Preset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PresetScreenViewModel(presetId: Int?, db: AppDatabase) :ViewModel() {
    //appdatabase
    private lateinit var _db: AppDatabase
    //preset id
    private var _presetId: Long? = null
    //preset name
    private val _presetName = MutableStateFlow("")
    val presetName: StateFlow<String> = _presetName.asStateFlow()
    fun updatePresetName(presetName: String){
        _presetName.value = presetName
    }

    //preset description
    private val _presetDescription = MutableStateFlow("")
    val presetDescription: StateFlow<String> = _presetDescription.asStateFlow()
    fun updatePresetDescription(presetDescription: String){
        _presetDescription.value = presetDescription
    }

    //function for saving preset
    fun savePresetToDatabase(){
        if(_presetId != null){
            val preset = Preset(_presetId!!, _presetName.value, _presetDescription.value)
            viewModelScope.launch {
                _db.presetDao().updatePreset(preset = preset)
            }
        }else{
            val preset = Preset(0, _presetName.value, _presetDescription.value)
            viewModelScope.launch {
                _db.presetDao().insertPreset(preset = preset)
            }
        }
    }

    //function for saving preset temporary for editing the map setting in collectionareascreen
    //returns the id of the preset
    fun savePresetTemporaryToDatabase(): Long{
        if(_presetId != null){
            viewModelScope.launch{
                _db.presetDao().updatePreset(Preset(_presetId!!, _presetName.value, _presetDescription.value))
            }
        }else{
            viewModelScope.launch {
                _presetId = _db.presetDao().insertPreset(Preset(0, _presetName.value, _presetDescription.value))
            }
        }
        return _presetId!!
    }

    init{
        _db = db
        //check whether id is not null and fetch the preset from database
        if(presetId != null){
            _presetId = presetId.toLong()
            viewModelScope.launch {
                val preset = db.presetDao().getPresetById(presetId = presetId)
                _presetName.value = preset.presetName
                _presetDescription.value = preset.presetDescription
            }
        }
    }
}