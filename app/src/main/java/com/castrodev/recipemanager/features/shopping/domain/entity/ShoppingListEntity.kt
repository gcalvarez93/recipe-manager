// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/domain/entity/ShoppingListEntity.kt
package com.castrodev.recipemanager.features.shopping.domain.entity

data class ShoppingListEntity(
    val id: String,
    val name: String,
    val year: Int,
    val week: Int,
    val items: List<ShoppingListItemEntity>,
    val createdAt: String
)

data class ShoppingListItemEntity(
    val name: String,
    val amount: String,
    val unit: String,
    val isChecked: Boolean
)