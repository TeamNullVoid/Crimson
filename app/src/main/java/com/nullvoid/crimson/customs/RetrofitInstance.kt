package com.nullvoid.crimson.customs

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Constant.FCM_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api: FcmApi by lazy {
            retrofit.create(FcmApi::class.java)
        }
    }

}