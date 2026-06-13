// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/data/datasource/ShoppingListApiService.kt
package com.castrodev.recipemanager.features.shopping.data.datasource

import com.castrodev.recipemanager.features.shopping.data.model.ShoppingListModel
import retrofit2.Response
import retrofit2.http.*

interface ShoppingListApiService {
    @GET("api/recipes/shopping/{year}/{week}")
    suspend fun getShoppingList(@Path("year") year: Int, @Path("week") week: Int): ShoppingListModel

    @POST("api/recipes/shopping/{year}/{week}/generate")
    suspend fun generateShoppingList(@Path("year") year: Int, @Path("week") week: Int): String

    @POST("api/recipes/shopping/{id}/toggle/{itemName}")
    suspend fun toggleItem(
        @Path("id") id: String,
        @Path("itemName", encoded = true) itemName: String
    ): Response<Unit>
}