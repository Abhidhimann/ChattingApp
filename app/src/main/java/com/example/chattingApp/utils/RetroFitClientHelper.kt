package com.example.chattingApp.utils

import com.example.chattingApp.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class RetroFitClientHelper() {
    private var retrofit: Retrofit? = null

    fun getApiClient(baseUrl: String, headers: Headers? = null): Retrofit {
        val gson = GsonBuilder().setLenient().create()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient =
            OkHttpClient.Builder()
                .readTimeout(Api.TIME_OUT.getValue().toLong(), TimeUnit.SECONDS)
                .connectTimeout(Api.TIME_OUT.getValue().toLong(), TimeUnit.SECONDS)
                .writeTimeout(Api.TIME_OUT.getValue().toLong(), TimeUnit.SECONDS)
                .apply {
                    if (BuildConfig.DEBUG){
//                        addInterceptor(logging)
                    }
                    addInterceptor { chain ->
                        val newRequest: Request = if (headers != null) {
                            chain.request().newBuilder()
                                .headers(headers)
                                .build()
                        } else {
                            chain.request().newBuilder()
                                .build()
                        }
                        chain.proceed(newRequest)
                    }
                }
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
