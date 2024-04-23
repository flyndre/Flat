package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng

data class CollectionArea(var color: Color, var isSelected: Boolean, var listAreaPoints: ArrayList<LatLng>)
