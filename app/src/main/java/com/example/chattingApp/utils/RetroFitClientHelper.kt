package com.example.chattingApp.utils

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class RetroFitClientHelper() {
    private var retrofit: Retrofit? = null

    fun getApiClient(baseUrl: String): Retrofit {
        val gson = GsonBuilder().setLenient().create()

        val okHttpClient =
            OkHttpClient.Builder()
                .readTimeout(Api.TIME_OUT.getValue().toLong(), TimeUnit.SECONDS)
                .connectTimeout(Api.TIME_OUT.getValue().toLong(), TimeUnit.SECONDS)
                .writeTimeout(Api.TIME_OUT.getValue().toLong(), TimeUnit.SECONDS)
                .build()


        if (retrofit == null) {
            retrofit = synchronized(this) {
                Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson)).build()
            }
        }

        return retrofit ?: throw IllegalStateException("Retrofit instance not initialized")
    }
}
