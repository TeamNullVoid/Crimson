package com.nullvoid.crimson.customs

data class FcmPayload(
    val notification: Notification,
    val to: String,
)