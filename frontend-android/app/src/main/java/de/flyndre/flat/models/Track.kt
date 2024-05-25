package de.flyndre.flat.models

import android.location.Location
import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

data class Track(
    var trackId: UUID = UUID.randomUUID(),
    var positions:ArrayList<Position> = arrayListOf()
) {
    private var _positions: ArrayList<Position> = arrayListOf()
    val distance: Double
        get(){
            var dis = 0.0
            for(i in 0..<positions.lastIndex){
                val result = floatArrayOf(0F,0F,0F)
                Location.distanceBetween(
                    positions[i].latitude,
                    positions[i].longitude,
                    positions[i+1].latitude,
                    positions[i+1].longitude,
                    result
                )
                dis+=result[0]
            }
            return dis
        }
    fun toLineString():LineString{
        if(positions.size<2){
            return LineString(listOf(positions.last(),positions.last()))
        }
        return LineString(positions)
    }

    fun deepCopy(): Track {
        val copy = Track()
        copy.trackId = UUID.fromString(this.trackId.toString())
        this.positions.forEach {
            copy.positions.add(Position(it.coordinates))
        }
        return copy
    }


}