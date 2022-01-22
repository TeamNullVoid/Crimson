package com.nullvoid.crimson.data.model

import java.io.Serializable

data class CrimsonUser(
    var userId: String? = null,
    var userName: String? = null,
    var userPhone: String? = null,
    var photoUri: String? = null,
) : Serializable