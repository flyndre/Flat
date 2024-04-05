package de.flyndre.flat.models

import io.github.dellisd.spatialk.geojson.MultiLineString
import java.util.UUID

class TrackCollection(var id:UUID = UUID.randomUUID()): ArrayList<Track>() {
    fun toMultiLineString(): MultiLineString {
        return MultiLineString(this.filter { x -> x.size >= 2 })
    }
}
