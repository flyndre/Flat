package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

data class CollectionArea(val color: Color, val isSelected: Boolean, val listAreaPoints: ArrayList<LatLng>)

fun generateBounds(collectionArea: CollectionArea): LatLngBounds {
    var north: Double = collectionArea.listAreaPoints.get(0).latitude
    var east: Double = collectionArea.listAreaPoints.get(0).longitude
    var south: Double = collectionArea.listAreaPoints.get(0).latitude
    var west: Double = collectionArea.listAreaPoints.get(0).longitude

    for(point in collectionArea.listAreaPoints){
        if(point.longitude > north){
            north = point.longitude
        }
        if(point.longitude < south){
            south = point.longitude
        }
        if(point.latitude > east){
            east = point.latitude
        }
        if(point.latitude < west){
            west = point.latitude
        }
    }

    val bounds = LatLngBounds(LatLng(west, south), LatLng(east, north))
    return bounds
}
