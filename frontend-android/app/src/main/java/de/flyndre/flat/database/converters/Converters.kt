package de.flyndre.flat.database.converters

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromListLatLng(list: ArrayList<LatLng>): String{
        val gson: Gson = Gson()
        val json: String = gson.toJson(list, object: TypeToken<ArrayList<LatLng>>(){}.type)
        return json
    }

    @TypeConverter
    fun toListLatLng(json: String): ArrayList<LatLng>{
        val gson: Gson = Gson()
        val list = gson.fromJson<ArrayList<LatLng>>(json, object: TypeToken<ArrayList<LatLng>>(){}.type)
        return list
    }
}