package de.flyndre.flat.models

import io.github.dellisd.spatialk.geojson.MultiLineString
import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

class TrackCollection(var clientId:UUID = UUID.randomUUID()): ArrayList<Track>() {
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

    fun addIncrementalTrack(trackId: UUID, track: LineString) {
        if(this.none { x -> x.trackId == trackId }){
            this.add(Track(trackId))
        }
        this.findLast { x->x.trackId==trackId }?.addAll(track.coordinates)
    }
}
