package de.flyndre.flat.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.Builder
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks.await
import de.flyndre.flat.interfaces.ILocationService
import io.github.dellisd.spatialk.geojson.Position

class LocationService(
    override val updateInterval: Long,
    private var fusedLocationClient: FusedLocationProviderClient,
    private val context: Context
) :ILocationService {
    override val onLocationUpdate: ArrayList<(Position) -> Unit> = arrayListOf()
    override var isTracking: Boolean = false
    private val locationRequest = Builder(updateInterval)
            .setIntervalMillis(updateInterval)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
    private var locationCallback = object :LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            p0.lastLocation?:return
            onLocationUpdate.forEach { callback->
                if(isTracking) {
                    callback(Position(p0.lastLocation!!.longitude, p0.lastLocation!!.latitude))
                }
            }
        }
    }



    override fun startTracking() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw Exception("No permissions to use location api!")
        }
        if(!isTracking){
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            isTracking=true
        }
    }

    override fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isTracking=false
    }

    override fun addOnLocationUpdate(callback: (Position) -> Unit) {
        onLocationUpdate.add(callback)
    }

    override suspend fun getCurrentPosition(): LatLng {
        val token = CancellationTokenSource().token
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw Exception("No permissions to use location api!")
        }
            val location = await(fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,token))
            return LatLng(location.latitude,location.longitude)
    }
}