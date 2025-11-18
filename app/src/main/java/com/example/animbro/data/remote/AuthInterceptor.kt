package com.example.animbro.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest = request.newBuilder()
            .addHeader("X-MAL-CLIENT-ID", CLIENT_ID)
            .build()

        return chain.proceed(newRequest)
    }

}