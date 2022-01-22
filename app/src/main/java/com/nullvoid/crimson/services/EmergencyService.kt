package com.nullvoid.crimson.services


import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nullvoid.crimson.EmergencyActivity
import com.nullvoid.crimson.R
import com.nullvoid.crimson.customs.DbHelper
import com.nullvoid.crimson.customs.FcmHelper
import com.nullvoid.crimson.customs.Global.Companion.buildNotification
import com.nullvoid.crimson.customs.LocationHelper
import com.nullvoid.crimson.data.CrimsonDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EmergencyService : Service() {

    private lateinit var locHelper: LocationHelper

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locHelper = LocationHelper(this)
        locHelper.initialize()
        GlobalScope.launch(Dispatchers.IO) {
            DbHelper(this@EmergencyService).setEmergency(true)
        }
        startForeground(2,
            buildNotification(this,
                Intent(this, EmergencyActivity::class.java),
                getString(R.string.emergency_alert),
                getString(R.string.emergency_service_triggered),
                "Emergency Channel"
            )
        )

        triggerAlert()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locHelper.stopUpdates()
        GlobalScope.launch(Dispatchers.IO) {
            DbHelper(this@EmergencyService).setEmergency(false)
        }
        stopForeground(true)
        stopSelf()
    }

    private fun sendSms(name: String?, lp: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            var message = "Emergency Alert\n${if (name == null) "I am" else "$name is"} in Emergency, Help!!!"
            try {
                if (lp) {
                    val loc = locHelper.getRecentLocation()
                    if (loc != null)
                        message += "\n\nSee my recent location at https://maps.google.com/?q=${loc.latitude},${loc.longitude}"
                }
            } finally {
                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    val manager = SmsManager.getDefault()
                    val list = CrimsonDatabase.getInstance(this@EmergencyService).crimsonUserDao.getAllUsers()
                    for (i in list) {
                        manager.sendTextMessage(i.basic.userPhone, null, message, null, null)
                    }
                }
            }
        }
    }

    private fun triggerAlert() {
        if (checkPermission()) {
            locHelper.startUpdates()
            FcmHelper(this).sendPushNotification(getString(R.string.emergency_alert),
                getString(R.string.triggered_alert, Firebase.auth.currentUser?.phoneNumber))
            sendSms(Firebase.auth.currentUser?.phoneNumber, true)
        } else {
            sendSms(Firebase.auth.currentUser?.phoneNumber, false)
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

}