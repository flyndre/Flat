package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

data class CollectionArea(val color: Color, val isSelected: Boolean, val listAreaPoints: ArrayList<LatLng>)

fun generateBounds(collectionArea: CollectionArea): LatLngBounds {
    val builder = LatLngBounds.builder()

    for(point in collectionArea.listAreaPoints){
        builder.include(point)
    }

    return builder.build()
}
