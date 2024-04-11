package de.flyndre.flat.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
data class Preset(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "presetName") val presetName: String,
    @ColumnInfo(name = "presetDescription") val presetDescription: String,
    @ColumnInfo(name = "presetAreaPoints") val presetAreaPoints: ArrayList<LatLng>
)
