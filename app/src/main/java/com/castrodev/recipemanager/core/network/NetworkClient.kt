// Path: app/src/main/java/com/castrodev/recipemanager/core/network/NetworkClient.kt
package com.castrodev.recipemanager.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private fun buildOkHttpClient(tokenProvider: TokenProvider): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun buildRetrofit(tokenProvider: TokenProvider): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(buildOkHttpClient(tokenProvider))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}