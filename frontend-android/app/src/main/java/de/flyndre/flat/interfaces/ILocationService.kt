package de.flyndre.flat.interfaces

interface ILocationService {
    var trackingService: ITrackingService
    fun startTracking()

    fun stopTracking()
}