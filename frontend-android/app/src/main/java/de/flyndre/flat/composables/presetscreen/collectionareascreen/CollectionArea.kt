package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng

data class CollectionArea(val color: Color, val isSelected: Boolean, val listAreaPoints: ArrayList<LatLng>)
