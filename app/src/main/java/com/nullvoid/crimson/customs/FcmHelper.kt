package com.nullvoid.crimson.customs

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FcmHelper(base: Context) : ContextWrapper(base) {

    fun sendPushNotification(title: String, message: String) {
        val topic = "/topics/${Firebase.auth.currentUser?.uid}"
        sendNotification(FcmPayload(
            to = topic,
            notification = Notification(
                title = title,
                body = message
            )
        ))
    }

    private fun sendNotification(notification: FcmPayload) = CoroutineScope(Dispatchers.IO).launch {
        try {
            RetrofitInstance.api.sendNotification(notification)
        } catch (e: Exception) {
            Log.e("Main", e.toString())
        }
    }

    fun unsubscribe(list:ArrayList<String?>) = CoroutineScope(Dispatchers.IO).launch {
        for (i in list){
            Firebase.messaging.unsubscribeFromTopic("/topics/$i")
        }
    }

    fun subscribe(list: ArrayList<String?>) {
        for (i in list){
            Firebase.messaging.subscribeToTopic("/topics/$i")
        }
    }

}