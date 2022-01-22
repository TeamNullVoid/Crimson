package com.nullvoid.crimson.customs

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.*
import com.google.firebase.Timestamp
import com.nullvoid.crimson.data.model.LocationExtras
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class LocationHelper(base: Context) : ContextWrapper(base) {

    private lateinit var dbHelper: DbHelper
    private lateinit var client: FusedLocationProviderClient
    private lateinit var request: LocationRequest

    private val callback = object : LocationCallback() {
        override fun onLocationResult(lr: LocationResult) {
            try {
                dbHelper.updateLocation(
                    LocationExtras(
                        lr.lastLocation.latitude,
                        lr.lastLocation.longitude,
                        lr.lastLocation.accuracy,
                        SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss",
                            Locale.ROOT
                        ).format(Timestamp.now().toDate())
                    )
                )
            } catch (e: Exception) {
            }
        }

        override fun onLocationAvailability(la: LocationAvailability) {
            return
        }
    }

    fun initialize() {
        client = LocationServices.getFusedLocationProviderClient(this)
        dbHelper = DbHelper(this)
        request = LocationRequest()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.interval = 10000
        request.fastestInterval = 5000
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun startUpdates() {
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    fun stopUpdates() {
        client.removeLocationUpdates(callback)
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    suspend fun getRecentLocation(): Location? {
        return client.lastLocation.await()
    }

}