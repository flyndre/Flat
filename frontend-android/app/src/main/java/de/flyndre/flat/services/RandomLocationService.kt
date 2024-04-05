package de.flyndre.flat.services

import de.flyndre.flat.interfaces.ILocationService
import de.flyndre.flat.interfaces.ITrackingService
import io.github.dellisd.spatialk.geojson.Position
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class RandomLocationService(override var trackingService: ITrackingService, var interval:Long = 5000) : ILocationService {
    var lat1 = 48.414400
    var lon1 =  8.490597
    var lat2 = 48.424255
    var lon2 = 8.506647
    var lastlat = ThreadLocalRandom.current().nextDouble(lat1,lat2)
    var lastlon = ThreadLocalRandom.current().nextDouble(lon1,lon2)
    var thread = Thread(Runnable {
        while(!Thread.interrupted()) {
            lastlon = ThreadLocalRandom.current().nextDouble(lastlon-0.00001,lastlon+0.0001)
            lastlat = ThreadLocalRandom.current().nextDouble(lastlat-0.00001,lastlat+0.0001)
            trackingService.addNewPosition(
                Position(
                    lastlon,
                    lastlat
                )
            )
            Thread.sleep(interval)
        }
    })

    override fun startTracking() {
        thread.start()

    }

    override fun stopTracking() {
        thread.interrupt()
    }
}