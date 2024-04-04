package de.flyndre.flat.models

import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

class Track(var trackId: UUID): ArrayList<Position>() {

    fun toLineString():LineString{
        return LineString(this)
    }

}