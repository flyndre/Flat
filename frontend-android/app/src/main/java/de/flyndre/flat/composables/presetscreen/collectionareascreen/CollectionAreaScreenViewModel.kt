package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionAreaScreenViewModel() : ViewModel() {
    //list of points for selection area
    private val _oldListCollectionAreas: ArrayList<CollectionArea> = arrayListOf()
    private val _listCollectionAreas = MutableStateFlow<List<CollectionArea>>(listOf())
    var listCollectionAreas = _listCollectionAreas.asStateFlow()

    //saved camera position
    private val _cameraPosition = MutableStateFlow(CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F))
    val cameraPosition: StateFlow<CameraPosition> = _cameraPosition.asStateFlow()

    //used to clear viewModel for creation of new empty preset
    fun clearCollectionArea() {
        _cameraPosition.value = CameraPosition(LatLng(0.0, 0.0), 0F, 0F, 0F)
        _listCollectionAreas.value = listOf()
    }

    fun setCameraPosition(cameraPosition: CameraPosition) {
        _cameraPosition.value = cameraPosition
    }

    fun getCameraPosition(): CameraPosition {
        return _cameraPosition.value
    }

    fun animateCameraPositionToCollectionArea(collectionArea: CollectionArea, cameraPositionState: CameraPositionState){
        viewModelScope.launch(Dispatchers.Main) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(generateBounds(collectionArea = collectionArea), 10))
        }
    }

    fun setListAreas(list: ArrayList<CollectionArea>) {
        refreshOldValues(list)
        _listCollectionAreas.value = list
    }

    private fun refreshOldValues(list: ArrayList<CollectionArea>){
        for(area in list){
            val listOfPoints: ArrayList<LatLng> = arrayListOf()
            for(p in area.listAreaPoints){
                listOfPoints.add(LatLng(p.latitude, p.longitude))
            }
            _oldListCollectionAreas.add(CollectionArea(Color(area.color.value), name = area.name, isSelected = false, listOfPoints))
        }
    }

    fun saveChanges(){
        refreshOldValues(ArrayList(_listCollectionAreas.value))
    }

    fun discardChanges(){
        val tempList: ArrayList<CollectionArea> = arrayListOf()

        for(area in _oldListCollectionAreas){
            val listOfPoints: ArrayList<LatLng> = arrayListOf()
            for(p in area.listAreaPoints){
                listOfPoints.add(LatLng(p.latitude, p.longitude))
            }
            tempList.add(CollectionArea(Color(area.color.value), name = area.name, isSelected = false, listOfPoints))
        }

        _listCollectionAreas.value = tempList
    }

    fun getListAreas(): ArrayList<CollectionArea> {
        return ArrayList(_listCollectionAreas.value)
    }

    fun addNewCollectionArea(color: Color) {
        val arrayList: ArrayList<CollectionArea> = ArrayList(_listCollectionAreas.value)

        for (area in arrayList) {
            if (area.isSelected) {
                val tempArea = area.copy(isSelected = false)
                arrayList.remove(area)
                arrayList.add(tempArea)
            }
        }
        arrayList.add(
            CollectionArea(
                color = color,
                name = "new area",
                isSelected = true,
                listAreaPoints = arrayListOf()
            )
        )

        _listCollectionAreas.value = arrayList
    }

    fun checkNewCollectionIsEmpty() {
        val arrayList: ArrayList<CollectionArea> = ArrayList(_listCollectionAreas.value)

        for (area in arrayList) {
            if (area.isSelected) {
                if (area.listAreaPoints.size < 3) {
                    arrayList.remove(area)
                } else {
                    val tempArea = area.copy(isSelected = false)
                    arrayList.remove(area)
                    arrayList.add(tempArea)
                }
            }
        }

        _listCollectionAreas.value = arrayList
    }

    fun addCollectionAreaPoint(point: LatLng) {
        val listOfAreas: ArrayList<CollectionArea> = arrayListOf()

        for(area in _listCollectionAreas.value){
            val listOfPoints: ArrayList<LatLng> = arrayListOf()
            for(p in area.listAreaPoints){
                listOfPoints.add(LatLng(p.latitude, p.longitude))
            }
            val newArea = CollectionArea(Color(area.color.value), area.name, area.isSelected, listOfPoints)
            if(area.isSelected){
                newArea.listAreaPoints.add(point)
            }

            listOfAreas.add(newArea)
        }

        _listCollectionAreas.value = listOfAreas
    }

    fun removeLastCollectionAreaPoint() {
        val listOfAreas: ArrayList<CollectionArea> = arrayListOf()

        for(area in _listCollectionAreas.value){
            val listOfPoints: ArrayList<LatLng> = arrayListOf()
            for(point in area.listAreaPoints){
                listOfPoints.add(LatLng(point.latitude, point.longitude))
            }
            val newArea = CollectionArea(Color(area.color.value), area.name, area.isSelected, listOfPoints)
            if(area.isSelected && area.listAreaPoints.isNotEmpty()){
                newArea.listAreaPoints.removeLast()
            }

            listOfAreas.add(newArea)
        }

        _listCollectionAreas.value = listOfAreas
    }

    fun removeCollectionArea(collectionArea: CollectionArea){
        val arrayList: ArrayList<CollectionArea> = ArrayList(_listCollectionAreas.value)

        arrayList.remove(collectionArea)

        _listCollectionAreas.value = arrayList
    }

    fun setCollectionAreaName(collectionArea: CollectionArea, name: String){
        val arrayList: ArrayList<CollectionArea> = ArrayList(_listCollectionAreas.value)
        val index = arrayList.indexOf(collectionArea)
        arrayList.remove(collectionArea)
        val listOfPoints: ArrayList<LatLng> = arrayListOf()
        for(point in collectionArea.listAreaPoints){
            listOfPoints.add(LatLng(point.latitude, point.longitude))
        }
        arrayList.add(index, CollectionArea(Color(collectionArea.color.value), name, collectionArea.isSelected, listOfPoints))

        _listCollectionAreas.value = arrayList
    }
}