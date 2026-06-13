// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/data/datasource/RecipeApiService.kt
package com.castrodev.recipemanager.features.recipes.data.datasource

import com.castrodev.recipemanager.features.recipes.data.model.*
import retrofit2.http.*

interface RecipeApiService {
    @GET("api/recipes")
    suspend fun getRecipes(@Query("category") category: String? = null, @Query("favoritesOnly") favoritesOnly: Boolean? = null): List<RecipeModel>

    @GET("api/recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: String): RecipeModel

    @POST("api/recipes")
    suspend fun createRecipe(@Body request: CreateRecipeRequest): String

    @PUT("api/recipes/{id}")
    suspend fun updateRecipe(@Path("id") id: String, @Body request: CreateRecipeRequest)

    @DELETE("api/recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: String)

    @POST("api/recipes/{id}/favorite")
    suspend fun toggleFavorite(@Path("id") id: String)

    @GET("api/recipes/external/search")
    suspend fun searchExternalRecipes(@Query("q") query: String): List<ExternalRecipeModel>

    @POST("api/recipes/external/import")
    suspend fun importExternalRecipe(@Body request: ImportExternalRecipeRequest): String
}