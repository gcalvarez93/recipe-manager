// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/data/datasource/ShoppingListRemoteDataSource.kt
package com.castrodev.recipemanager.features.shopping.data.datasource

import com.castrodev.recipemanager.core.network.requireSuccess
import com.castrodev.recipemanager.features.shopping.data.model.ShoppingListModel
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import java.net.URLEncoder
import javax.inject.Inject

class ShoppingListRemoteDataSource @Inject constructor(
    private val api: ShoppingListApiService
) {

    suspend fun getShoppingList(year: Int, week: Int): ShoppingListModel? {
        return try {
            api.getShoppingList(year, week)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.w(e, "ShoppingListRemoteDataSource.getShoppingList: sin lista para %d/%d", year, week)
            null
        }
    }

    suspend fun generateShoppingList(year: Int, week: Int) = api.generateShoppingList(year, week)

    suspend fun toggleItem(id: String, itemName: String) {
        val encoded = URLEncoder.encode(itemName, "UTF-8").replace("+", "%20")
        api.toggleItem(id, encoded).requireSuccess()
    }
}