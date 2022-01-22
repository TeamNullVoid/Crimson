package com.nullvoid.crimson.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nullvoid.crimson.customs.FcmHelper

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private lateinit var fcmHelper: FcmHelper

    override fun onReceive(context: Context, intent: Intent) {
        fcmHelper = FcmHelper(context)
        val event = GeofencingEvent.fromIntent(intent)
        val geofences = event.triggeringGeofences
        if (!event.hasError()) {
            for (i in geofences) {
                when (event.geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        fcmHelper.sendPushNotification("Location Update",
                            "${Firebase.auth.currentUser?.displayName} reached ${i.requestId}")
                    }
                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        fcmHelper.sendPushNotification("Location Update",
                            "${Firebase.auth.currentUser?.displayName} left ${i.requestId}")
                    }
                }
            }
        }
    }
}