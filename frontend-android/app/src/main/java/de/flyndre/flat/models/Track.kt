package de.flyndre.flat.models

import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Polygon
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

class Track(var trackId: UUID = UUID.randomUUID()): ArrayList<Position>() {

    fun toLineString():LineString{
        if(size<2){
            return LineString(listOf(this.last(),this.last()))
        }
        return LineString(this)
    }



}