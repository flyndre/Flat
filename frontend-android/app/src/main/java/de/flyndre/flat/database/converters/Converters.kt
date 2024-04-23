package de.flyndre.flat.database.converters

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.flyndre.flat.composables.presetscreen.collectionareascreen.CollectionArea

class Converters {

    @TypeConverter
    fun fromListCollectionAreas(list: ArrayList<CollectionArea>): String{
        val gson: Gson = Gson()
        val json: String = gson.toJson(list, object: TypeToken<ArrayList<CollectionArea>>(){}.type)
        return json
    }

    @TypeConverter
    fun toListLatLng(json: String): ArrayList<CollectionArea>{
        val gson: Gson = Gson()
        val list = gson.fromJson<ArrayList<CollectionArea>>(json, object: TypeToken<ArrayList<CollectionArea>>(){}.type)
        return list
    }

    @TypeConverter
    fun fromCameraPosition(cameraPosition: CameraPosition): String{
        val gson: Gson = Gson()
        val json: String = gson.toJson(cameraPosition, object: TypeToken<CameraPosition>(){}.type)
        return json
    }

    @TypeConverter
    fun toCameraPosition(json: String): CameraPosition{
        val gson: Gson = Gson()
        val cameraPosition = gson.fromJson<CameraPosition>(json, object: TypeToken<CameraPosition>(){}.type)
        return cameraPosition
    }
}