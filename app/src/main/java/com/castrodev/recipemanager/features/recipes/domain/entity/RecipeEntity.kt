// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/domain/entity/RecipeEntity.kt
package com.castrodev.recipemanager.features.recipes.domain.entity

data class RecipeEntity(
    val id: String, val name: String, val description: String, val category: String,
    val imageUrl: String, val prepTimeMinutes: Int, val cookTimeMinutes: Int,
    val servings: Int, val difficulty: String,
    val ingredients: List<IngredientEntity>, val steps: List<RecipeStepEntity>,
    val tags: List<String>, val isFavorite: Boolean, val externalId: String?,
    val isImported: Boolean, val createdAt: String
)

data class IngredientEntity(val name: String, val amount: String, val unit: String)
data class RecipeStepEntity(val order: Int, val description: String)

data class ExternalRecipeEntity(
    val externalId: String, val name: String, val category: String, val area: String,
    val instructions: String, val imageUrl: String,
    val ingredients: List<IngredientEntity>, val youtubeUrl: String?
)