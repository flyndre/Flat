package de.flyndre.flat.models

import androidx.compose.foundation.pager.PagerSnapDistance
import io.github.dellisd.spatialk.geojson.MultiLineString
import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Position
import java.util.UUID

data class TrackCollection(
    var clientId:UUID = UUID.randomUUID(),
    var tracks: ArrayList<Track> = arrayListOf()
) {
    val distance : Double
        get() {
            var dis = 0.0
            tracks.forEach {
                dis+=it.distance
            }
            dis = dis.times(100)
            dis = dis.toInt().toDouble()
            dis = dis.div(100)
            return dis
        }
    fun toMultiLineString(): MultiLineString {
        val list : ArrayList<ArrayList<Position>> = arrayListOf()
        tracks.forEach { x->
            if(x.positions.size<2){
                list.add(arrayListOf(x.positions.last(),x.positions.last()))
            }
            else{
                list.add(x.positions)
            }
        }
        return MultiLineString(list)
    }

    fun addIncrementalTrack(trackId: UUID, track: LineString) {
        if(tracks.none { x -> x.trackId == trackId }){
            tracks.add(Track(trackId))
        }
        if(track.coordinates.size==2&& track.coordinates[0] == track.coordinates[0]){
            tracks.findLast { x->x.trackId==trackId }?.positions?.add(track.coordinates.first())
            return
        }
        if(track.coordinates.first()==tracks.findLast { x->x.trackId==trackId }?.positions?.last()){
            tracks.findLast { x->x.trackId==trackId }?.positions?.addAll(track.coordinates-track.coordinates.first())
            return
        }
        tracks.findLast { x->x.trackId==trackId }?.positions?.addAll(track.coordinates)
    }

    fun deepCopy(): TrackCollection {
        val copy = TrackCollection()
        copy.clientId= UUID.fromString(this.clientId.toString())
        this.tracks.forEach {
            copy.tracks.add(it.deepCopy())
        }
        return copy
    }
}
