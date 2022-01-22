package com.nullvoid.crimson.customs

import com.nullvoid.crimson.data.model.FcmPayload
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FcmApi {

    @Headers("Authorization: key=${Constant.SERVER_KEY}","Content-Type:${Constant.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: FcmPayload,
    ): Response<ResponseBody>

}