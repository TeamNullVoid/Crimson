package com.nullvoid.crimson.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationServices
import com.nullvoid.crimson.customs.GeofenceHelper

class GeofenceBootRegister : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val geofenceClient = LocationServices.getGeofencingClient(context)
            val geofenceHelper = GeofenceHelper(context)
        }
    }
}