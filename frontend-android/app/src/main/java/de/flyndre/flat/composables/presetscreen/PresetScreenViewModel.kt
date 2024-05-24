package de.flyndre.flat.composables.presetscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import io.github.dellisd.spatialk.geojson.Polygon
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PresetScreenViewModel(
    val db: AppDatabase,
    collectionAreaScreenViewModel: CollectionAreaScreenViewModel,
    trackingScreenViewModel: TrackingScreenViewModel,
    connectionService: IConnectionService,
) : ViewModel() {

    //collectionAreaScreenViewModel
    private var _collectionAreaScreenViewModel = collectionAreaScreenViewModel
    private var _trackingScreenViewModel = trackingScreenViewModel
    private var _db = db

    //preset id
    private var _presetId: Long = 0

    //preset name
    private val _presetName = MutableStateFlow("")
    val presetName: StateFlow<String> = _presetName.asStateFlow()

    //preset description
    private val _presetDescription = MutableStateFlow("")
    val presetDescription: StateFlow<String> = _presetDescription.asStateFlow()

    private val _connectionService = connectionService


    fun clearPresetValues(){
        _presetId = 0
        _presetName.value = ""
        _presetDescription.value = ""
        _collectionAreaScreenViewModel.clearCollectionArea()
    }

    fun setPresetValues(id: Long, name: String, description: String, collectionAreas: ArrayList<CollectionArea>, cameraPosition: CameraPosition){
        _presetId = id
        _presetName.value = name
        _presetDescription.value = description
        _collectionAreaScreenViewModel.setListAreas(collectionAreas)
        _collectionAreaScreenViewModel.setCameraPosition(cameraPosition)
    }

    fun getId(): Long{
        return _presetId
    }

    fun getName(): String{
        return _presetName.value
    }

    fun getDescription(): String{
        return _presetDescription.value
    }

    fun getCollectionArea(): ArrayList<CollectionArea> {
        return _collectionAreaScreenViewModel.getListAreas()
    }

    fun getCameraPosition(): CameraPosition{
        return _collectionAreaScreenViewModel.getCameraPosition()
    }

    fun updatePresetName(presetName: String) {
        _presetName.value = presetName
    }

    fun updatePresetDescription(presetDescription: String) {
        _presetDescription.value = presetDescription
    }

    fun savePresetToDatabase(){
        viewModelScope.launch(Dispatchers.IO) {
            val preset = Preset(_presetId, _presetName.value, _presetDescription.value, _collectionAreaScreenViewModel.getListAreas(), _collectionAreaScreenViewModel.getCameraPosition())
            if(_presetId == 0.toLong()){
                _presetId = _db.presetDao().insertPreset(preset)
            }else{
                _db.presetDao().updatePreset(preset)
            }
        }
    }

    //publish collection to backend
    fun openCollection(onSuccess: (() -> Unit), onFailure: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            savePresetToDatabase()
            try {
                //open the collection
                val result = _connectionService.openCollection(
                    _presetName.value,
                    MultiPolygon(
                        arrayListOf(
                            _collectionAreaScreenViewModel.getListAreas().map { area ->
                                area.listAreaPoints.map { point ->
                                    Position(point.longitude, point.latitude)
                                }
                            })
                    )
                )
                _trackingScreenViewModel.collectionInstance = result

                //convert collection areas from collectionAreaViewModel for server
                val collectionAreaModelList = arrayListOf<de.flyndre.flat.models.CollectionArea>()
                for (area in _collectionAreaScreenViewModel.getListAreas()) {
                    val positionList = arrayListOf<Position>()
                    for (point in area.listAreaPoints) {
                        positionList.add(Position(point.longitude, point.latitude))
                    }
                    val multiPolygon =
                        Polygon(coordinates = listOf(positionList), null)
                    val collectionArea = de.flyndre.flat.models.CollectionArea(
                        multiPolygon,
                        name = area.name,
                        color = "#" + Integer.toHexString((area.color.red*255).toInt()) + Integer.toHexString((area.color.green*255).toInt()) + Integer.toHexString((area.color.blue*255).toInt())
                    )
                    collectionAreaModelList.add(collectionArea)
                }
                //upload collection areas to server
                if(result.id !=null){
                    _trackingScreenViewModel.collectionInstance = _connectionService.setAreaDivision(result.id!!, collectionAreaModelList)
                }else{
                    throw RequestFailedException("result doesn't contain an id:$result")
                }

                onSuccess()
            } catch (e: RequestFailedException) {
                if (onFailure != null) {
                    e.message?.let { onFailure(it) }
                }
            }
        }
    }
}
class PresetScreenViewModelFactory(val db: AppDatabase,
                                   val collectionAreaScreenViewModel: CollectionAreaScreenViewModel,
                                   val trackingScreenViewModel: TrackingScreenViewModel,
                                   val connectionService: IConnectionService)
    : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PresetScreenViewModel(db,collectionAreaScreenViewModel,trackingScreenViewModel,connectionService) as T
    }
    }