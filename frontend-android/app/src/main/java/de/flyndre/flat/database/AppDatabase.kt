package de.flyndre.flat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.flyndre.flat.database.converters.Converters
import de.flyndre.flat.database.daos.PresetDao
import de.flyndre.flat.database.entities.Preset

@Database(entities = [Preset::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun presetDao(): PresetDao
}