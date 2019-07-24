package com.ismael.movieapp.api

import com.ismael.movieapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response


class ImodbInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val oldUrl = request.url()

        val url = oldUrl.newBuilder()
            .addQueryParameter("apikey", BuildConfig.API_KEY)
            .build()

        // Request customization: add request headers
        val requestBuilder = request.newBuilder()
            .url(url)

        val build = requestBuilder.build()
        return chain.proceed(build)
    }
}