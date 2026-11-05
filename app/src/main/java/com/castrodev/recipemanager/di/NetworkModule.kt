// Path: app/src/main/java/com/castrodev/recipemanager/di/NetworkModule.kt
package com.castrodev.recipemanager.di

import com.castrodev.recipemanager.BuildConfig
import com.castrodev.recipemanager.core.network.ApiConfig
import com.castrodev.recipemanager.core.network.AuthInterceptor
import com.castrodev.recipemanager.core.network.SessionEvents
import com.castrodev.recipemanager.core.network.TokenAuthenticator
import com.castrodev.recipemanager.core.network.TokenProvider
import com.castrodev.recipemanager.features.mealplan.data.datasource.MealPlanApiService
import com.castrodev.recipemanager.features.profile.data.datasource.ProfileApiService
import com.castrodev.recipemanager.features.recipes.data.datasource.RecipeApiService
import com.castrodev.recipemanager.features.shopping.data.datasource.ShoppingListApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TIMEOUT_SECONDS = 30L

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenProvider: TokenProvider,
        sessionEvents: SessionEvents
    ): OkHttpClient {
        // Los cuerpos de peticiones y respuestas solo se registran en debug, y nunca el token.
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader("Authorization")
        }
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(logging)
            // Ante un 401 renueva el token y reintenta una vez; si no basta, avisa de sesión caducada.
            .authenticator(TokenAuthenticator(tokenProvider, sessionEvents))
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRecipeApiService(retrofit: Retrofit): RecipeApiService =
        retrofit.create(RecipeApiService::class.java)

    @Provides
    @Singleton
    fun provideMealPlanApiService(retrofit: Retrofit): MealPlanApiService =
        retrofit.create(MealPlanApiService::class.java)

    @Provides
    @Singleton
    fun provideShoppingListApiService(retrofit: Retrofit): ShoppingListApiService =
        retrofit.create(ShoppingListApiService::class.java)

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)
}