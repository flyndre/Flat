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
        setListAreas(_oldListCollectionAreas)
    }

    fun getListAreas(): ArrayList<CollectionArea> {
        return ArrayList(_listCollectionAreas.value)
    }

    fun checkNewCollectionIsEmpty() {
        if(_selectedArea.value!=null&& _selectedArea.value!!.listAreaPoints.size<3){
            setAreaList(_listCollectionAreas.value-_selectedArea.value!!)
        }
    }

    fun addCollectionAreaPoint(point: LatLng,toUpdate:LatLng?=null) {
        _selectedPoint.value = point
        val area = _selectedArea.value
        if (area != null) {
            area.listAreaPoints.add(point)
            val new = CollectionArea(area.color, arrayListOf())
            area.listAreaPoints.forEach { new.listAreaPoints.add(LatLng(it.latitude,it.longitude)) }
            _selectedArea.value = null
            _selectedArea.value = new
        }

        val list = _listCollectionAreas.value
        list.forEach { area ->
            if(area== _selectedArea.value){
                if(toUpdate!=null){
                    val index = area.listAreaPoints.indexOf(point)
                    if(index !=-1){
                        area.listAreaPoints.removeAt(index)
                        area.listAreaPoints.add(index,point)
                    }
                }else{
                    area.listAreaPoints.add(LatLng(point.latitude,point.longitude))
                }
            }
        }
        setAreaList(list)
    }

    fun removeLastCollectionAreaPoint() {
        _listCollectionAreas.value.findLast {
            it==_selectedArea.value
        }?.listAreaPoints?.removeLast()
        setAreaList(_listCollectionAreas.value)
    }

    fun removeCollectionArea(collectionArea: CollectionArea){
        setAreaList(_listCollectionAreas.value-collectionArea)
    }

    fun addPoint(point:LatLng){
        addCollectionAreaPoint(point)
    }

    fun setSelectedPoint(point: LatLng?,cameraPositionState: CameraPositionState?) {
        _selectedPoint.value=point;
        viewModelScope.launch {
            point?.let { CameraUpdateFactory.newLatLng(it) }
                ?.let { cameraPositionState?.animate(it) }
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

    fun addArea(area:CollectionArea?=null) {
        if(area!=null){
            setAreaList(_listCollectionAreas.value+area)
        }else{
            setAreaList(_listCollectionAreas.value+CollectionArea(Color.Blue, arrayListOf()))
        }
        _selectedArea.value= _listCollectionAreas.value.last()
    }

    private fun setAreaList(areas: List<CollectionArea>){
        val newAreaList = arrayListOf<CollectionArea>()
        areas.forEach { area ->
            val newPointList = arrayListOf<LatLng>()
            area.listAreaPoints.forEach {
            newPointList.add(LatLng(it.latitude,it.longitude))
                if(it==_selectedPoint.value){
                    _selectedPoint.value = newPointList.last()
                }
            }
            newAreaList.add(CollectionArea(Color(area.color.value),newPointList))
            if(area==_selectedArea.value){
                _selectedArea.value=newAreaList.last()
            }
        }
        _listCollectionAreas.value = newAreaList + CollectionArea(Color.White, arrayListOf())
        _listCollectionAreas.value = newAreaList
    }
}