package de.flyndre.flat.models

import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Polygon
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

data class Track(var trackId: UUID = UUID.randomUUID(),var positions:ArrayList<Position> = arrayListOf()) {

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