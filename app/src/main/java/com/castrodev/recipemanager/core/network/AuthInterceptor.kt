// Path: app/src/main/java/com/castrodev/recipemanager/core/network/AuthInterceptor.kt
package com.castrodev.recipemanager.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenProvider.getToken() }
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}