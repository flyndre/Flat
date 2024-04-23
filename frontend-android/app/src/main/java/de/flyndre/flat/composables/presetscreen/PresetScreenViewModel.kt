package de.flyndre.flat.composables.presetscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import de.flyndre.flat.composables.presetscreen.collectionareascreen.CollectionArea
import de.flyndre.flat.composables.presetscreen.collectionareascreen.CollectionAreaScreenViewModel
import de.flyndre.flat.composables.trackingscreen.TrackingScreenViewModel
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.database.entities.Preset
import de.flyndre.flat.exceptions.RequestFailedException
import de.flyndre.flat.interfaces.IConnectionService
import io.github.dellisd.spatialk.geojson.MultiPolygon
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PresetScreenViewModel(db: AppDatabase, collectionAreaScreenViewModel: CollectionAreaScreenViewModel,trackingScreenViewModel: TrackingScreenViewModel, connectionService: IConnectionService) :ViewModel() {
    //appdatabase
    private var _db = db
    //collectionAreaScreenViewModel
    private var _collectionAreaScreenViewModel = collectionAreaScreenViewModel
    private var _trackingScreenViewModel = trackingScreenViewModel
    //preset id
    private var _presetId: Long = 0

    private val _connectionService = connectionService

    fun newEmptyPreset(){
        _presetId = 0
        _presetName.value = ""
        _presetDescription.value = ""
        _collectionAreaScreenViewModel.newEmptyCollectionArea()
    }

    fun setPresetId(presetId: Long){
        _presetId = presetId
        viewModelScope.launch {
            val preset = _db.presetDao().getPresetById(presetId = presetId)
            _presetName.value = preset.presetName
            _presetDescription.value = preset.presetDescription
            _collectionAreaScreenViewModel.setListAreas(preset.presetCollectionAreas)
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
        val preset = Preset(_presetId, _presetName.value, _presetDescription.value, _collectionAreaScreenViewModel.getListAreas(), _collectionAreaScreenViewModel.getCameraPosition())
        viewModelScope.launch {
            if(_presetId == 0.toLong()){
                _db.presetDao().insertPreset(preset = preset)
            }else{
                _db.presetDao().updatePreset(preset = preset)
            }
        }
    }

    fun getCameraPosition(): CameraPosition {
        return _collectionAreaScreenViewModel.getCameraPosition()
    }

    fun getCollectionArea(): ArrayList<CollectionArea>{
        return _collectionAreaScreenViewModel.getListAreas()
    }

    //publish collection to backend
    fun openCollection(onSuccess:(()->Unit),onFailure:((String)->Unit)?=null){
        viewModelScope.launch {
            savePresetToDatabase()
            try{
                val result = _connectionService.openCollection(
                    _presetName.value,
                    MultiPolygon(arrayListOf(_collectionAreaScreenViewModel.getListAreas().map {
                      area ->area.listAreaPoints.map{
                        point -> Position(point.longitude,point.latitude)
                      }
                    }))
                )
                _trackingScreenViewModel.collectionInstance=result
                onSuccess()
            }catch (e:RequestFailedException){
                if (onFailure != null) {
                    e.message?.let { onFailure(it) }
                }
            }
        }
    }
}