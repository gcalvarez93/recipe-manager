// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/domain/repository/ShoppingListRepository.kt
package com.castrodev.recipemanager.features.shopping.domain.repository

import com.castrodev.recipemanager.features.shopping.domain.entity.ShoppingListEntity

interface ShoppingListRepository {
    suspend fun getShoppingList(year: Int, week: Int): Result<ShoppingListEntity?>
    suspend fun generateShoppingList(year: Int, week: Int): Result<String>
    suspend fun toggleItem(id: String, itemName: String): Result<Unit>
}