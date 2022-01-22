package com.nullvoid.crimson.customs

import android.location.Location

interface LocationListener {
    fun onLocationChanged(location: Location)
    fun onProviderDisabled(provider: String)
    fun onProviderEnabled(provider: String)
    fun onStatusChanged(provider: String?, status: Int)
    fun onPermissionDenied()
}