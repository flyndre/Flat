package de.flyndre.flat.composables.presetscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.database.entities.Preset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PresetScreenViewModel(db: AppDatabase) :ViewModel() {
    //appdatabase
    private var _db = db
    //preset id
    private var _presetId: Long = 0
    fun getPresetId(): Long{
        return _presetId
    }

    fun setPresetId(presetId: Long){
        _presetId = presetId
        if(presetId == 0.toLong()){
            viewModelScope.launch {
                _presetName.value = ""
                _presetDescription.value = ""
                _presetAreaPoints.value = arrayListOf()
                _presetId = _db.presetDao().insertPreset(Preset(_presetId, _presetName.value, _presetDescription.value, _presetAreaPoints.value))
            }
        }else{
            viewModelScope.launch {
                val preset = _db.presetDao().getPresetById(presetId = presetId)
                _presetName.value = preset.presetName
                _presetDescription.value = preset.presetDescription
                _presetAreaPoints.value = preset.presetAreaPoints
            }
        }
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

    //preset area points
    private val _presetAreaPoints: MutableStateFlow<ArrayList<LatLng>> = MutableStateFlow(arrayListOf())
    val presetAreaPoints: StateFlow<List<LatLng>> = _presetAreaPoints.asStateFlow()

    fun savePresetToDatabase(){
        viewModelScope.launch {
            _db.presetDao().updatePreset(Preset(_presetId, _presetName.value, _presetDescription.value, _presetAreaPoints.value))
        }
    }
}