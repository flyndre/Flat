package de.flyndre.flat.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import de.flyndre.flat.database.entities.Preset
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("Select * From preset")
    fun getAll(): Flow<List<Preset>>
    @Query("Select * From preset Where id = :presetId")
    fun getPresetById(presetId: Int): Preset
    @Update
    fun updatePreset(preset: Preset)
}