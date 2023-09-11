package com.condor.splashbox.network

import okhttp3.Interceptor
import okhttp3.Response

class CustomHeaderInterceptor(
    private var headerName: String,
    private var headerValue: String
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val modifiedRequest = originalRequest.newBuilder()
                .addHeader(headerName, headerValue)
                .build()

        val response = chain.proceed(modifiedRequest)
        return response
    }
}