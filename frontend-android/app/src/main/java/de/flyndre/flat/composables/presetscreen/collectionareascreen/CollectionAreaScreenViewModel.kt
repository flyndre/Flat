package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CollectionAreaScreenViewModel(): ViewModel() {
    //list of points for selection area
    var listCollectionAreas = mutableStateListOf<CollectionArea>()

    //saved camera position
    private val _cameraPosition = MutableStateFlow(CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F))
    val cameraPosition: StateFlow<CameraPosition> = _cameraPosition.asStateFlow()

    //used to clear viewModel for creation of new empty preset
    fun newEmptyCollectionArea(){
        _cameraPosition.value = CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F)
        listCollectionAreas.clear()
    }

    fun setCameraPosition(cameraPosition: CameraPosition){
        _cameraPosition.value = cameraPosition
    }

    fun getCameraPosition(): CameraPosition{
        return _cameraPosition.value
    }

    fun setListAreas(list: ArrayList<CollectionArea>){
        listCollectionAreas.clear()
        listCollectionAreas.addAll(list)
    }

    fun getListAreas(): ArrayList<CollectionArea>{
        return ArrayList(listCollectionAreas)
    }

    fun addNewCollectionArea(color: Color){
        for(area in listCollectionAreas){
            if(area.isSelected){
                area.isSelected = false
            }
        }
        listCollectionAreas.add(CollectionArea(color = color, isSelected = true, listAreaPoints = arrayListOf()))
    }

    fun checkNewCollectionIsEmpty(){
        for(area in listCollectionAreas){
            if(area.isSelected){
                if(area.listAreaPoints.size < 3){
                    listCollectionAreas.remove(area)
                }else{
                    area.isSelected = false
                }
            }
        }
    }

    fun addPCollectionAreaPoint(point: LatLng){
        for(area in listCollectionAreas){
            if(area.isSelected){
                area.listAreaPoints.add(point)
                break
            }
        }
    }

    fun removeLastCollectionAreaPoint(){
        for(area in listCollectionAreas){
            if(area.isSelected){
                if(area.listAreaPoints.isNotEmpty()){
                    area.listAreaPoints.removeLast()
                }
                break
            }
        }
    }
}