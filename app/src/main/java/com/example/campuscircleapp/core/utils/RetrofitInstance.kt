package com.example.campuscircleapp.core.utils

import com.example.campuscircleapp.core.interceptor.LoaderInterceptor
import com.example.campuscircleapp.core.services.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://campus-circle.hserver321.dpdns.org/api/"

    // Create an interceptor to log requests and responses
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Log the full body (request payload and response)
        level = HttpLoggingInterceptor.Level.BODY
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