package de.flyndre.flat.composables.collectionareascreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.database.entities.Preset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionAreaScreenViewModel(db: AppDatabase): ViewModel() {
    //appdatabase
    private var _db = db
    //preset id
    private var _presetId: Long = 0

    fun setPresetId(presetId: Long){
        _presetId = presetId

        viewModelScope.launch {
            _listAreaPoints.value = _db.presetDao().getPresetById(presetId = presetId).presetAreaPoints
        }
    }

    //list of points for selection area
    private val _listAreaPoints: MutableStateFlow<ArrayList<LatLng>> = MutableStateFlow(arrayListOf())
    val listAreaPoints: StateFlow<List<LatLng>> = _listAreaPoints.asStateFlow()

    fun addPCollectionAreaPoint(point: LatLng){
        val newList = arrayListOf<LatLng>()
        newList.addAll(_listAreaPoints.value)
        newList.add(point)
        _listAreaPoints.value = newList
    }

    fun removeLastCollectionAreaPoint(){
        if(_listAreaPoints.value.isNotEmpty()){
            val newList = arrayListOf<LatLng>()
            newList.addAll(_listAreaPoints.value)
            newList.removeLast()
            _listAreaPoints.value = newList
        }
    }

    fun clearCollectionArea(){
        _listAreaPoints.value = arrayListOf<LatLng>()
    }

    fun saveCollectionAreaToPreset(){
        viewModelScope.launch {
            var preset = _db.presetDao().getPresetById(presetId = _presetId)
            preset = Preset(preset.id, preset.presetName, preset.presetDescription, presetAreaPoints = _listAreaPoints.value)
            _db.presetDao().updatePreset(preset)
        }
    }
}