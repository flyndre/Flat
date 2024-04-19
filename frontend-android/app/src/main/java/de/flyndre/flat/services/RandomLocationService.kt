package de.flyndre.flat.services

import android.util.Log
import de.flyndre.flat.interfaces.ILocationService
import io.github.dellisd.spatialk.geojson.Position
import java.util.concurrent.ThreadLocalRandom

class RandomLocationService(
    override val updateInterval: Long = 5000,
    override val onLocationUpdate: ArrayList<(Position) -> Unit> = arrayListOf()
) : ILocationService {
    override var isTracking: Boolean = false
    private var lat1 = 48.414400
    private var lon1 =  8.490597
    private var lat2 = 48.424255
    private var lon2 = 8.506647
    private var lastlat = ThreadLocalRandom.current().nextDouble(lat1,lat2)
    private var lastlon = ThreadLocalRandom.current().nextDouble(lon1,lon2)
    private lateinit var thread :Thread

    override fun startTracking() {
        try {
            thread = Thread {
                try {
                    while (!Thread.interrupted()) {
                        lastlon = ThreadLocalRandom.current()
                            .nextDouble(lastlon - 0.0001, lastlon + 0.0001)
                        lastlat = ThreadLocalRandom.current()
                            .nextDouble(lastlat - 0.0001, lastlat + 0.0001)
                        onLocationUpdate.stream().forEach { x ->
                            x(
                                Position(
                                    lastlon,
                                    lastlat
                                )
                            )
                        }
                        Thread.sleep(updateInterval)
                    }
                } catch (_: InterruptedException) {
                }
            }
            thread.start()
            isTracking = true
        }catch (ex:Exception){
            Log.e(this.toString(),ex.toString())
        }

    }

    override fun stopTracking() {
        thread.interrupt()
        isTracking=false
    }

    override fun addOnLocationUpdate(callback: (Position) -> Unit) {
        onLocationUpdate.add(callback)
    }
}