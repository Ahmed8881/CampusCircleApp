package com.example.campuscircleapp.core.interceptor

import com.example.campuscircleapp.shared.services.LoaderManager
import okhttp3.Interceptor
import okhttp3.Response

class LoaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val skipLoader = request.header("No-Loader") == "true"

        if (!skipLoader) LoaderManager.show()
        return try {
            chain.proceed(request)
        } finally {
            if (!skipLoader) LoaderManager.hide()
        }
    }
}