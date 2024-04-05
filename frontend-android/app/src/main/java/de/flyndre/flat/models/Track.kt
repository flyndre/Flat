package de.flyndre.flat.models

import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

class Track(var trackId: UUID = UUID.randomUUID()): ArrayList<Position>() {

    fun toLineString():LineString{
        if(size<2){
            return LineString(Position(0.0,0.0),Position(0.0,0.0))
        }
        return LineString(this)
    }

}