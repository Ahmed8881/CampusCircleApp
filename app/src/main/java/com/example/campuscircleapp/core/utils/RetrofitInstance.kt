package com.example.campuscircleapp.core.utils

import com.example.campuscircleapp.BuildConfig
import com.example.campuscircleapp.core.interceptor.LoaderInterceptor
import com.example.campuscircleapp.core.services.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://campus-circle.hserver321.dpdns.org/api/"

    // Log requests only in debug builds to reduce overhead
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
                else HttpLoggingInterceptor.Level.NONE
    }

    // Attach the interceptor to OkHttpClient
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(LoaderInterceptor())
        .build()

    val api : ApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <-- Use the custom OkHttp client here
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    }
}