package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CollectionAreaScreenViewModel(): ViewModel() {
    //list of points for selection area
    private val _listCollectionAreas = MutableStateFlow<List<CollectionArea>>(listOf())
    var listCollectionAreas = _listCollectionAreas.asStateFlow()

    //saved camera position
    private val _cameraPosition = MutableStateFlow(CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F))
    val cameraPosition: StateFlow<CameraPosition> = _cameraPosition.asStateFlow()

    //used to clear viewModel for creation of new empty preset
    fun newEmptyCollectionArea(){
        _cameraPosition.value = CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F)
        _listCollectionAreas.value = listOf()
    }

    fun setCameraPosition(cameraPosition: CameraPosition){
        _cameraPosition.value = cameraPosition
    }

    fun getCameraPosition(): CameraPosition{
        return _cameraPosition.value
    }

    fun setListAreas(list: ArrayList<CollectionArea>){
        _listCollectionAreas.value = list
    }

    fun getListAreas(): ArrayList<CollectionArea>{
        return ArrayList(_listCollectionAreas.value)
    }

    fun addNewCollectionArea(color: Color){
        val arrayList: ArrayList<CollectionArea> = ArrayList(_listCollectionAreas.value)

        for(area in arrayList){
            if(area.isSelected){
                val tempArea = area.copy(isSelected = false)
                arrayList.remove(area)
                arrayList.add(tempArea)
            }
        }
        arrayList.add(CollectionArea(color = color, isSelected = true, listAreaPoints = arrayListOf()))

        _listCollectionAreas.value = arrayList
    }

    fun checkNewCollectionIsEmpty(){
        val arrayList: ArrayList<CollectionArea> = ArrayList(_listCollectionAreas.value)

        for(area in arrayList){
            if(area.isSelected){
                if(area.listAreaPoints.size < 3){
                    arrayList.remove(area)
                }else{
                    val tempArea = area.copy(isSelected = false)
                    arrayList.remove(area)
                    arrayList.add(tempArea)
                }
            }
        }

        _listCollectionAreas.value = arrayList
    }

    fun addPCollectionAreaPoint(point: LatLng){
        val arrayList: ArrayList<CollectionArea> = ArrayList(_listCollectionAreas.value)

        for(area in arrayList){
            if(area.isSelected){
                val tempArea = area.copy()
                tempArea.listAreaPoints.add(point)
                arrayList.remove(area)
                arrayList.add(tempArea)
                break
            }
        }

        _listCollectionAreas.value = arrayList
    }

    fun removeLastCollectionAreaPoint(){
        val arrayList: ArrayList<CollectionArea> = ArrayList(_listCollectionAreas.value)

        for(area in arrayList){
            if(area.isSelected){
                if(area.listAreaPoints.isNotEmpty()){
                    val tempArea = area.copy()
                    tempArea.listAreaPoints.removeLast()
                    arrayList.remove(area)
                    arrayList.add(tempArea)
                }
                break
            }
        }

        _listCollectionAreas.value = arrayList
    }
}