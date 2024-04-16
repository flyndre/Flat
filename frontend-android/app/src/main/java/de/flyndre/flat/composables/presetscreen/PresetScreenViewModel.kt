package de.flyndre.flat.composables.presetscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import de.flyndre.flat.composables.presetscreen.collectionareascreen.CollectionAreaScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.database.entities.Preset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PresetScreenViewModel(db: AppDatabase, collectionAreaScreenViewModel: CollectionAreaScreenViewModel) :ViewModel() {
    //appdatabase
    private var _db = db
    //collectionAreaScreenViewModel
    private var _collectionAreaScreenViewModel = collectionAreaScreenViewModel
    //preset id
    private var _presetId: Long = 0
    fun setPresetId(presetId: Long){
        _presetId = presetId
        viewModelScope.launch {
            val preset = _db.presetDao().getPresetById(presetId = presetId)
            _presetName.value = preset.presetName
            _presetDescription.value = preset.presetDescription
            _collectionAreaScreenViewModel.setListAreaPoints(preset.presetAreaPoints)
            _collectionAreaScreenViewModel.setCameraPosition(preset.presetCameraPosition)
        }
    }
    fun getPresetId(): Long{
        return _presetId
    }
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
        val preset = Preset(_presetId, _presetName.value, _presetDescription.value, _collectionAreaScreenViewModel.getListAreaPoints(), _collectionAreaScreenViewModel.getCameraPosition())
        viewModelScope.launch {
            if(_presetId == 0.toLong()){
                _db.presetDao().insertPreset(preset = preset)
            }else{
                _db.presetDao().updatePreset(preset = preset)
            }
        }
    }
}