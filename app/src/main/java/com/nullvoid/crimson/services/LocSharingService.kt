package com.nullvoid.crimson.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nullvoid.crimson.MainActivity
import com.nullvoid.crimson.R
import com.nullvoid.crimson.customs.FcmHelper
import com.nullvoid.crimson.customs.Global.Companion.buildNotification
import com.nullvoid.crimson.customs.LocationHelper

class LocSharingService : Service() {

    private lateinit var locHelper: LocationHelper

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locHelper = LocationHelper(this)
        locHelper.initialize()

        startForeground(1,
            buildNotification(this,
                Intent(this, MainActivity::class.java),
                getString(R.string.sharing_location),
                getString(R.string.live_location_sharing_is_active),
                "Location Sharing Channel"))
        shareLocation()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun shareLocation() {
        if (checkPermission()) {
            locHelper.startUpdates()
            FcmHelper(this).sendPushNotification(getString(R.string.location_updates),
                getString(R.string.msg_started_loc_share, Firebase.auth.currentUser?.phoneNumber))
        } else {
            stopForeground(true)
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locHelper.stopUpdates()
        stopForeground(true)
        stopSelf()
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

}