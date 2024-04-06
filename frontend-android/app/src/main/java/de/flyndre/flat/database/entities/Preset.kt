package de.flyndre.flat.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Preset(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "presetName") val presetName: String,
    @ColumnInfo(name = "presetDescription") val presetDescription: String
)
