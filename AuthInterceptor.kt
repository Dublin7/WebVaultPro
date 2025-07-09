package com.devvaultpro.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import com.devvaultpro.auth.TokenManager

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenManager.getToken(context)
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "token $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
