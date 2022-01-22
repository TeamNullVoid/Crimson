package com.nullvoid.crimson.customs

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng
import com.nullvoid.crimson.receivers.GeofenceBroadcastReceiver

class GeofenceHelper(base: Context) : ContextWrapper(base) {

    private var pI: PendingIntent? = null

    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    fun getGeofence(Id: String, latLng: LatLng, radius: Float?, transitionTypes: Int): Geofence {
        return Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, radius!!)
            .setRequestId(Id)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun getPendingIntent(): PendingIntent? {
        if (pI == null) {
            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
            pI = PendingIntent.getBroadcast(this, Constant.BC_GEOFENCE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        return pI
    }

    fun errorMessage(e: Exception): String {
        return if (e is ApiException)
            when (e.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "Geofence not available"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Too many Geofence"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Too many requests"
                else -> e.message.toString()
            }
        else "Unknown Error"
    }
}