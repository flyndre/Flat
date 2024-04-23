package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CollectionAreaScreenViewModel(): ViewModel() {
    //list of points for selection area
    private val _listCollectionAreas = MutableStateFlow<ArrayList<CollectionArea>>(arrayListOf())
    var listCollectionAreas = _listCollectionAreas.asStateFlow()

    //saved camera position
    private val _cameraPosition = MutableStateFlow(CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F))
    val cameraPosition: StateFlow<CameraPosition> = _cameraPosition.asStateFlow()

    //used to clear viewModel for creation of new empty preset
    fun newEmptyCollectionArea(){
        _cameraPosition.value = CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F)
        _listCollectionAreas.value.clear()
    }

    fun setCameraPosition(cameraPosition: CameraPosition){
        _cameraPosition.value = cameraPosition
    }

    fun getCameraPosition(): CameraPosition{
        return _cameraPosition.value
    }

    fun setListAreas(list: ArrayList<CollectionArea>){
        _listCollectionAreas.value.clear()
        _listCollectionAreas.value.addAll(list)
    }

    fun getListAreas(): ArrayList<CollectionArea>{
        return ArrayList(_listCollectionAreas.value)
    }

    fun addNewCollectionArea(color: Color){
        _listCollectionAreas.update {
            for(area in it){
                if(area.isSelected){
                    val tempArea = area.copy(isSelected = false)
                    it.remove(area)
                    it.add(tempArea)
                }
            }
            it.add(CollectionArea(color = color, isSelected = true, listAreaPoints = arrayListOf()))
            it
        }
    }

    fun checkNewCollectionIsEmpty(){
        _listCollectionAreas.update {
            for(area in it){
                if(area.isSelected){
                    if(area.listAreaPoints.size < 3){
                        it.remove(area)
                    }else{
                        val tempArea = area.copy(isSelected = false)
                        it.remove(area)
                        it.add(tempArea)
                    }
                }
            }
            it
        }
    }

    fun addPCollectionAreaPoint(point: LatLng){
        _listCollectionAreas.update {
            for(area in it){
                if(area.isSelected){
                    val tempArea = area.copy()
                    tempArea.listAreaPoints.add(point)
                    it.remove(area)
                    it.add(tempArea)
                    break
                }
            }
            it
        }
    }

    fun removeLastCollectionAreaPoint(){
        _listCollectionAreas.update {
            for(area in it){
                if(area.isSelected){
                    if(area.listAreaPoints.isNotEmpty()){
                        val tempArea = area.copy()
                        tempArea.listAreaPoints.removeLast()
                        it.remove(area)
                        it.add(tempArea)
                    }
                    break
                }
            }
            it
        }
    }
}