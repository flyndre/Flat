package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.database.entities.Preset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionAreaScreenViewModel(): ViewModel() {
    //list of points for selection area
    val listAreaPoints = mutableStateListOf<CollectionArea>()

    //saved camera position
    private val _cameraPosition = MutableStateFlow(CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F))
    val cameraPosition: StateFlow<CameraPosition> = _cameraPosition.asStateFlow()

    //used to clear viewModel for creation of new empty preset
    fun newEmptyCollectionArea(){
        _cameraPosition.value = CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F)
        listAreaPoints.clear()
    }

    fun setCameraPosition(cameraPosition: CameraPosition){
        _cameraPosition.value = cameraPosition
    }

    fun getCameraPosition(): CameraPosition{
        return _cameraPosition.value
    }

    fun setListAreaPoints(list: ArrayList<LatLng>){
        _listAreaPoints.value = list
    }

    fun getListAreaPoints(): ArrayList<LatLng>{
        return _listAreaPoints.value
    }

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
}