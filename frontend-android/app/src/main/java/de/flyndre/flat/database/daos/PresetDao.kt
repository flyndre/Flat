package de.flyndre.flat.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.flyndre.flat.database.entities.Preset
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("Select * FROM preset")
    fun getAll(): Flow<List<Preset>>
}