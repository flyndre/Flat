package de.flyndre.flat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.flyndre.flat.database.daos.PresetDao
import de.flyndre.flat.database.entities.Preset

@Database(entities = [Preset::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase(){
    abstract fun presetDao(): PresetDao
}