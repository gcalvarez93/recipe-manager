// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/data/repository/ShoppingListRepositoryImpl.kt
package com.castrodev.recipemanager.features.shopping.data.repository

import com.castrodev.recipemanager.core.util.safeCall
import com.castrodev.recipemanager.features.shopping.data.datasource.ShoppingListRemoteDataSource
import com.castrodev.recipemanager.features.shopping.domain.entity.ShoppingListEntity
import com.castrodev.recipemanager.features.shopping.domain.repository.ShoppingListRepository
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(
    private val dataSource: ShoppingListRemoteDataSource
) : ShoppingListRepository {
    override suspend fun getShoppingList(year: Int, week: Int): Result<ShoppingListEntity?> =
        safeCall("ShoppingListRepositoryImpl.getShoppingList") { dataSource.getShoppingList(year, week)?.toEntity() }

    override suspend fun generateShoppingList(year: Int, week: Int): Result<String> =
        safeCall("ShoppingListRepositoryImpl.generateShoppingList") { dataSource.generateShoppingList(year, week) }

    override suspend fun toggleItem(id: String, itemName: String): Result<Unit> =
        safeCall("ShoppingListRepositoryImpl.toggleItem") { dataSource.toggleItem(id, itemName) }
}