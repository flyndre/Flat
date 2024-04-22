package de.flyndre.flat.models

import io.github.dellisd.spatialk.geojson.MultiLineString
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

class TrackCollection(var id:UUID = UUID.randomUUID()): ArrayList<Track>() {
    fun toMultiLineString(): MultiLineString {
        val list : ArrayList<ArrayList<Position>> = arrayListOf()
        this.forEach { x->
            if(x.size<2){
                list.add(arrayListOf(x.last(),x.last()))
            }
            else{
                list.add(x)
            }
        }
        return MultiLineString(list)
    }
}
