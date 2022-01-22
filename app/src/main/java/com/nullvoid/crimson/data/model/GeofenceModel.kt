package com.nullvoid.crimson.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GeofenceModel(
    @PrimaryKey
    var name: String,
    var latitude: Double,
    var longitude: Double,
    var radius: Double,
)
