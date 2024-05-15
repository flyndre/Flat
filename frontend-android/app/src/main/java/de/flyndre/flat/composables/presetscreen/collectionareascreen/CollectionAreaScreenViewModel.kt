package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
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

    private val _selectedPoint = MutableStateFlow<LatLng?>(null)
    val selectedPoint: StateFlow<LatLng?> = _selectedPoint.asStateFlow()
    private val _selectedArea = MutableStateFlow<CollectionArea?>(null)
    val selectedArea: StateFlow<CollectionArea?> = _selectedArea.asStateFlow()

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
            _oldListCollectionAreas.add(CollectionArea(Color(area.color.value), listOfPoints))
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
            tempList.add(CollectionArea(Color(area.color.value), listOfPoints))
        }

        _listCollectionAreas.value = tempList
    }

    fun getListAreas(): ArrayList<CollectionArea> {
        return ArrayList(_listCollectionAreas.value)
    }

    fun addNewCollectionArea(color: Color) {
        val arrayList: ArrayList<CollectionArea> = ArrayList(_listCollectionAreas.value)
        val newArea = CollectionArea(
            color = color,
            listAreaPoints = arrayListOf()
        )
        arrayList.add(
            newArea
        )
        setSelectedArea(newArea)
        _listCollectionAreas.value = arrayList
    }

    fun checkNewCollectionIsEmpty() {
        val arrayList: ArrayList<CollectionArea> = ArrayList(_listCollectionAreas.value)
        if(_selectedArea.value!=null&& _selectedArea.value!!.listAreaPoints.size<3){
            arrayList.remove(_selectedArea.value)
        }
        _listCollectionAreas.value = arrayList
    }

    fun addCollectionAreaPoint(point: LatLng,toUpdate:LatLng?=null) {
        val listOfAreas: ArrayList<CollectionArea> = arrayListOf()

        for(area in _listCollectionAreas.value){
            val listOfPoints: ArrayList<LatLng> = arrayListOf()
            for(p in area.listAreaPoints){
                listOfPoints.add(LatLng(p.latitude, p.longitude))
            }
            val newArea = CollectionArea(Color(area.color.value), listOfPoints)

            if(toUpdate==null) {
                if(area==_selectedArea.value){
                    newArea.listAreaPoints.add(point)
                }
            }else{
                if(area.listAreaPoints.contains(toUpdate)) {
                    val index = newArea.listAreaPoints.indexOf(toUpdate)
                    newArea.listAreaPoints.remove(toUpdate)
                    newArea.listAreaPoints.add(index, point)
                }

            }
            if(_selectedArea.value!=null&&_selectedArea.value==area){
                _selectedArea.value=newArea
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
            val newArea = CollectionArea(Color(area.color.value),listOfPoints)
            if(area==_selectedArea.value && area.listAreaPoints.isNotEmpty()){
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

    fun addPoint(point:LatLng){
        addCollectionAreaPoint(point)
    }

    fun setSelectedPoint(point: LatLng?,cameraPositionState: CameraPositionState) {
        _selectedPoint.value=point;
        viewModelScope.launch {
            point?.let { CameraUpdateFactory.newLatLng(it) }
                ?.let { cameraPositionState.animate(it) }
        }
    }

    fun setSelectedArea(area:CollectionArea?,cameraPositionState: CameraPositionState?=null){
        _selectedArea.value=area
        viewModelScope.launch {
            if(area != null){
                val builder = LatLngBounds.builder()
                for(position in area.listAreaPoints){
                    builder.include(position)
                }
                viewModelScope.launch(Dispatchers.Main) {
                    cameraPositionState?.animate(CameraUpdateFactory.newLatLngBounds(builder.build(), 10))
                }
            }
        }

    }

    fun updatePoint(selectedPoint: LatLng, target: LatLng) {
        addCollectionAreaPoint(target,selectedPoint)
        _selectedPoint.value = target
    }
}