// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/data/model/ShoppingListModel.kt
package com.castrodev.recipemanager.features.shopping.data.model

import com.castrodev.recipemanager.features.shopping.domain.entity.ShoppingListEntity
import com.castrodev.recipemanager.features.shopping.domain.entity.ShoppingListItemEntity

data class ShoppingListModel(
    val id: String = "",
    val name: String = "",
    val year: Int = 0,
    val week: Int = 0,
    val items: List<ShoppingListItemModel> = emptyList(),
    val createdAt: String = ""
) {
    fun toEntity() = ShoppingListEntity(
        id = id, name = name, year = year, week = week,
        items = items.map { ShoppingListItemEntity(it.name, it.amount, it.unit, it.isChecked) },
        createdAt = createdAt
    )
}

data class ShoppingListItemModel(
    val name: String = "",
    val amount: String = "",
    val unit: String = "",
    val isChecked: Boolean = false
)