// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/data/datasource/ShoppingListRemoteDataSource.kt
package com.castrodev.recipemanager.features.shopping.data.datasource

import com.castrodev.recipemanager.core.network.FirebaseTokenProvider
import com.castrodev.recipemanager.core.network.NetworkClient
import com.castrodev.recipemanager.features.shopping.data.model.ShoppingListModel
import java.net.URLEncoder

class ShoppingListRemoteDataSource {
    private val api = NetworkClient.buildRetrofit(FirebaseTokenProvider()).create(ShoppingListApiService::class.java)

    suspend fun getShoppingList(year: Int, week: Int): ShoppingListModel? {
        return try { api.getShoppingList(year, week) } catch (e: Exception) { null }
    }

    suspend fun generateShoppingList(year: Int, week: Int) = api.generateShoppingList(year, week)

    suspend fun toggleItem(id: String, itemName: String) {
        val encoded = URLEncoder.encode(itemName, "UTF-8").replace("+", "%20")
        api.toggleItem(id, encoded)
    }
}