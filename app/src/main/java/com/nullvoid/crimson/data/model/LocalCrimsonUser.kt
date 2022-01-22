package com.nullvoid.crimson.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class LocalCrimsonUser(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @Embedded var basic: CrimsonUser,
    @Embedded var locationExtras: LocationExtras?,
    @Embedded var crimsonExtras: CrimsonExtras?,
) : Serializable
