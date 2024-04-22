package de.flyndre.flat.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import de.flyndre.flat.composables.presetscreen.collectionareascreen.CollectionArea

@Entity
data class Preset(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "presetName") val presetName: String,
    @ColumnInfo(name = "presetDescription") val presetDescription: String,
    @ColumnInfo(name = "presetCollectionAreas") val presetCollectionAreas: ArrayList<CollectionArea>,
    @ColumnInfo(name = "presetCameraPosition") val presetCameraPosition: CameraPosition
)
