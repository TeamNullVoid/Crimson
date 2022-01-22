package com.nullvoid.crimson.data.model

data class FcmPayload(
    val notification: Notification,
    val to: String,
)
