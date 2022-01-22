package com.nullvoid.crimson.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class StopLocService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopService(Intent(this, LocSharingService::class.java))
        stopSelf()
        return START_NOT_STICKY
    }

}