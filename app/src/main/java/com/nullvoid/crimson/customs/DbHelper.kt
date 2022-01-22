package com.nullvoid.crimson.customs

import android.content.Context
import android.content.ContextWrapper
import com.google.firebase.Timestamp
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullvoid.crimson.data.CrimsonDatabase
import com.nullvoid.crimson.data.model.CrimsonExtras
import com.nullvoid.crimson.data.model.CrimsonUser
import com.nullvoid.crimson.data.model.LocalCrimsonUser
import com.nullvoid.crimson.data.model.LocationExtras
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class DbHelper(base: Context) : ContextWrapper(base) {

    suspend fun loginSetup() {
        val u = Firebase.auth.currentUser!!
        val user = CrimsonUser(
            userId = u.uid,
            userPhone = u.phoneNumber?.toLowerCase(Locale.ROOT),
            userName = u.displayName,
            photoUri = u.photoUrl.toString()
        )

        if (!fetchData("users/${u.uid}").exists()) {
            postData("users/${u.uid}", user)
            postData("users/${u.uid}/private/extras",
                CrimsonExtras(false))
            postData("users/${u.uid}/private/users", hashMapOf("users" to arrayListOf<String>()))
            postData("users/${u.uid}/private/requests", hashMapOf("requests" to arrayListOf<String>()))
            postData("requests/${u.uid}", hashMapOf("requests" to arrayListOf<String>()))
        }
        reloadFriendsData()
    }

    private suspend fun postLocData(data: Any) {
        val u = Firebase.auth.currentUser!!
        Firebase.database.getReference("location/${u.uid}").setValue(data).await()
    }

    private suspend fun postData(path: String, data: Any) {
        Firebase.firestore.document(path).set(data).await()
    }

    private suspend fun fetchData(path: String): DocumentSnapshot {
        return Firebase.firestore.document(path).get().await()
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun fetchFriendList(): ArrayList<String?> {
        val u = Firebase.auth.currentUser!!
        return fetchData("users/${u.uid}/private/users").get("users") as ArrayList<String?>
    }

    suspend fun reloadFriendsData() {
        val list = fetchFriendList()
        val db = CrimsonDatabase.getInstance(this)
        val friends = arrayListOf<LocalCrimsonUser>()
        for (i in list) {
            val user = fetchData("users/$i").toObject(CrimsonUser::class.java)!!
            val extras = fetchData("users/$i/private/extras").toObject(CrimsonExtras::class.java)
            val locCrimsonUser = LocalCrimsonUser(
                basic = user,
                locationExtras = null,
                crimsonExtras = extras,
            )
            friends.add(locCrimsonUser)
        }
        db.crimsonUserDao.clear()
        db.crimsonUserDao.insertAll(friends)
        FcmHelper(this).subscribe(list)
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun loadRequests(): ArrayList<CrimsonUser> {
        val u = Firebase.auth.currentUser!!
        val requests: ArrayList<CrimsonUser> = arrayListOf()
        val ids = fetchData("requests/${u.uid}").get("requests") as ArrayList<String>
        for (i in ids) {
            requests.add(fetchData("users/$i").toObject(CrimsonUser::class.java)!!)
        }
        return requests
    }

    @Throws(Exception::class)
    suspend fun sendRequest(phone: String) {
        val query =
            Firebase.firestore.collection("users").whereEqualTo("userPhone", phone.toLowerCase(Locale.ROOT)).get()
                .await()
        if (query.documents.isNotEmpty()) {
            val uid = query.documents[0].data?.get("userId")
            setRequest(uid as String)
        } else {
            throw Exception("Crimson user with this Phone Number doesn't exist")
        }
    }

    suspend fun allowRequest(uid: String?) {
        val myId = Firebase.auth.currentUser?.uid
        updateField("users/$uid/private/users", "users", FieldValue.arrayUnion(myId))
        updateField("users/$myId/private/users", "users", FieldValue.arrayUnion(uid))
        updateField("requests/$myId", "requests", FieldValue.arrayRemove(uid))
        updateField("users/$uid/private/requests", "requests", FieldValue.arrayRemove(myId))

        val user = fetchData("users/$uid").toObject(CrimsonUser::class.java)!!
        val extras = fetchData("users/$uid/private/extras").toObject(CrimsonExtras::class.java)
        val lsu = LocalCrimsonUser(basic = user, locationExtras = null, crimsonExtras = extras)
        CrimsonDatabase.getInstance(this).crimsonUserDao.insert(lsu)
        FcmHelper(this).subscribe(arrayListOf(uid))
    }

    suspend fun setEmergency(value: Boolean) {
        val myId = Firebase.auth.currentUser?.uid
        Firebase.firestore.document("users/$myId/private/extras").update("inEmergency", value).await()
    }

    suspend fun denyRequest(uid: String?) {
        val myId = Firebase.auth.currentUser?.uid
        updateField("requests/$myId", "requests", FieldValue.arrayRemove(uid))
        updateField("users/$uid/private/requests", "requests", FieldValue.arrayRemove(myId))
    }

    private suspend fun updateField(path: String, field: String, value: Any) {
        Firebase.firestore.document(path).update(field, value).await()
    }

    private suspend fun setRequest(uid: String) {
        val myId = Firebase.auth.currentUser?.uid
        updateField("requests/$uid", "requests", FieldValue.arrayUnion(myId))
        updateField("users/$myId/private/requests", "requests", FieldValue.arrayUnion(uid))
    }

    suspend fun removeFriend(uid: String) {
        FcmHelper(this).unsubscribe(arrayListOf(uid))
        val myId = Firebase.auth.currentUser?.uid
        updateField("users/$myId/private/users", "users", FieldValue.arrayRemove(uid))
        updateField("users/$uid/private/users", "users", FieldValue.arrayRemove(myId))
        CrimsonDatabase.getInstance(this).crimsonUserDao.delete(uid)
    }

    fun updateLocation(extras: LocationExtras) = CoroutineScope(Dispatchers.IO).launch {
        postLocData(extras)
    }

}