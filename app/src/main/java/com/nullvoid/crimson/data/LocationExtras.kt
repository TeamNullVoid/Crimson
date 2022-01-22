package com.nullvoid.crimson.data

import java.io.Serializable

data class LocationExtras(
    var latitude: Double? = null,
    var longitude: Double? = null,
    var accuracy: Float? = null,
    var lastUpdate: String? = null,
) : Serializable