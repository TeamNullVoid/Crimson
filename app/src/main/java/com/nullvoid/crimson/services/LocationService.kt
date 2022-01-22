package com.nullvoid.crimson.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.nullvoid.crimson.customs.LocationListener

class LocationService : Service() {

    private lateinit var locationManager: LocationManager
    private lateinit var listener: LocationListener
    private var gpsLoc: Location? = null
    private var netLoc: Location? = null

    inner class LocationBinder : Binder() {
        fun instance(): LocationService {
            return this@LocationService
        }
    }

    private val locationListener = object : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            if (location.provider == LocationManager.GPS_PROVIDER) gpsLoc = location
            else if (location.provider == LocationManager.NETWORK_PROVIDER) netLoc = location
            val finalLoc =
                if (gpsLoc != null && netLoc != null) {
                    if (gpsLoc?.accuracy!! > netLoc?.accuracy!!) netLoc!! else gpsLoc!!
                } else if (gpsLoc == null && netLoc != null) netLoc as Location
                else if (gpsLoc != null && netLoc == null) gpsLoc as Location
                else location
            listener.onLocationChanged(finalLoc)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            listener.onStatusChanged(provider, status)
        }

        override fun onProviderEnabled(provider: String) {
            if (provider == LocationManager.GPS_PROVIDER)
                listener.onProviderEnabled(provider)
            else listener.onProviderEnabled(provider)
        }

        override fun onProviderDisabled(provider: String) {
            if (provider == LocationManager.GPS_PROVIDER)
                listener.onProviderDisabled(provider)
            else listener.onProviderDisabled(provider)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocationBinder()
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun initialize(listener: LocationListener) {
        this.listener = listener
    }

    fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun startUpdates() {
        if (!hasPermission()) {
            listener.onPermissionDenied()
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            500,
            10f,
            locationListener
        )
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            500,
            10f,
            locationListener
        )
    }

    fun stopUpdates() {
        locationManager.removeUpdates(locationListener)
    }

    fun allProvidersDisabled(): Boolean {
        return !(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        ))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

}