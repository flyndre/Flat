package de.flyndre.flat.interfaces

import io.github.dellisd.spatialk.geojson.Position

interface ILocationService {
    val updateInterval:Long
    val onLocationUpdate : ArrayList<(Position)->Unit>
    var isTracking: Boolean
    fun startTracking()

    fun stopTracking()

    fun addOnLocationUpdate(callback:(Position)->Unit)
}