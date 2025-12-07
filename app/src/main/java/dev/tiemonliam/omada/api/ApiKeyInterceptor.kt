package dev.tiemonliam.omada.api

import dev.tiemonliam.omada.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) =
        chain.proceed(
        chain.request().newBuilder()
            .url(
                chain.request().url.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.FLICKR_API_KEY)
                    .build()
            )
            .build()
    )
}