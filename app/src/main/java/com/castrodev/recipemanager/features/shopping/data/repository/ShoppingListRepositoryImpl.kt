// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/data/repository/ShoppingListRepositoryImpl.kt
package com.castrodev.recipemanager.features.shopping.data.repository

import com.castrodev.recipemanager.features.shopping.data.datasource.ShoppingListRemoteDataSource
import com.castrodev.recipemanager.features.shopping.domain.entity.ShoppingListEntity
import com.castrodev.recipemanager.features.shopping.domain.repository.ShoppingListRepository

class ShoppingListRepositoryImpl(
    private val dataSource: ShoppingListRemoteDataSource = ShoppingListRemoteDataSource()
) : ShoppingListRepository {
    override suspend fun getShoppingList(year: Int, week: Int): Result<ShoppingListEntity?> =
        runCatching { dataSource.getShoppingList(year, week)?.toEntity() }

    override suspend fun generateShoppingList(year: Int, week: Int): Result<String> =
        runCatching { dataSource.generateShoppingList(year, week) }

    override suspend fun toggleItem(id: String, itemName: String): Result<Unit> =
        runCatching { dataSource.toggleItem(id, itemName) }
}