package com.nullvoid.crimson.customs

import android.Manifest

class Constant {
    companion object {
        const val PREF_DELETE ="key_delete"
        const val PREF_GUIDE = "key_guide"
        const val PREF_LOGOUT = "key_logout"
        const val PREF_PROFILE = "key_profile"
        const val RM_TIME = "RM_TIME"
        const val PREF_LOCALE = "key_locale"
        const val RC_NOTIFICATION = 4
        const val CHANNEL_ID = "NOT_CHANNEL"
        const val EXTRAS_ID = "ID_EXTRAS"
        const val RC_BG_LOC_PERM = 5
        const val BC_GEOFENCE = 10
        const val RC_PICK_CONTACT = 3
        const val RC_SIGN_IN = 2
        const val RC_LOC_PERM = 1
        const val DB_NAME = "sepia_db"
        const val RC_PERMISSION = 0
        const val KEY_FIRST_RUN = "key_firstRun"
        const val SP_CRIMSON = "sp_crimson"
        const val PREF_KEY_THEME = "key_theme"

        const val FCM_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAAnH0-d_M:APA91bHK9N-YiwWPcA8Jht0frE2AybgTcn9EjAqBlIVHH6L7QXCT9V_y3LqiEHpDH8vhowCbm1xraBrs9tNa6hhQdY6rraCDFiYnNYQfj0MJyLl9W8Zd2cLlnXNwjQ4ejoKjF4DnFd7A"
        const val CONTENT_TYPE = "application/json"

        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS
        )
    }
}