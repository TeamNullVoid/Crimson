package com.nullvoid.crimson.customs

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nullvoid.crimson.R

class Global {

    companion object {
        @Suppress("DEPRECATION")
        fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        fun buildNotification(
            context: Context,
            intent: Intent,
            title: String,
            msg: String,
            channelName: String
        ): Notification {
            createChannel(context, channelName)
            val pendingIntent =
                PendingIntent.getActivity(context, Constant.RC_NOTIFICATION, intent, 0)
            return NotificationCompat.Builder(context, Constant.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
        }

        private fun createChannel(context: Context, channelName: String) {
            val channel = NotificationChannel(
                Constant.CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        fun showMessage(context: Context) {
            val dialog = MaterialAlertDialogBuilder(context).setTitle(R.string.permission_denied)
            dialog.apply {
                setMessage(R.string.perm_denied_msg)
                setPositiveButton(R.string.settings) { _, _ ->
                    val intent =
                        Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }
                setNegativeButton(R.string.cancel, null)
            }
            dialog.show()
        }

        fun showLocationPermissionRational(context: Context): AlertDialog {
            val dialog = MaterialAlertDialogBuilder(context).setTitle(R.string.perm_req)
                .setMessage(R.string.locPermNeed)
                .setPositiveButton(R.string.ok, null)
                .setIcon(R.drawable.ic_location)
                .create()
            dialog.show()
            return dialog
        }

        fun showCallDialog(context: Context): AlertDialog {
            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(R.string.perm_req)
                .setMessage(R.string.call_perm_req)
                .setIcon(R.drawable.ic_call)
                .setPositiveButton(R.string.ok, null)
                .create()
            dialog.show()
            return dialog
        }

    }

}