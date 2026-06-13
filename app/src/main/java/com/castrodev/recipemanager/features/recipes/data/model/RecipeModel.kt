// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/data/model/RecipeModel.kt
package com.castrodev.recipemanager.features.recipes.data.model

import com.castrodev.recipemanager.features.recipes.domain.entity.*

data class RecipeModel(
    val id: String = "", val name: String = "", val description: String = "",
    val category: String = "", val imageUrl: String = "",
    val prepTimeMinutes: Int = 0, val cookTimeMinutes: Int = 0,
    val servings: Int = 1, val difficulty: String = "medium",
    val ingredients: List<IngredientModel> = emptyList(),
    val steps: List<RecipeStepModel> = emptyList(),
    val tags: List<String> = emptyList(), val isFavorite: Boolean = false,
    val externalId: String? = null, val isImported: Boolean = false,
    val createdAt: String = ""
) {
    fun toEntity() = RecipeEntity(
        id = id, name = name, description = description, category = category,
        imageUrl = imageUrl, prepTimeMinutes = prepTimeMinutes, cookTimeMinutes = cookTimeMinutes,
        servings = servings, difficulty = difficulty,
        ingredients = ingredients.map { IngredientEntity(it.name, it.amount, it.unit) },
        steps = steps.map { RecipeStepEntity(it.order, it.description) },
        tags = tags, isFavorite = isFavorite, externalId = externalId,
        isImported = isImported, createdAt = createdAt
    )
}

data class IngredientModel(val name: String = "", val amount: String = "", val unit: String = "")
data class RecipeStepModel(val order: Int = 0, val description: String = "")

data class ExternalRecipeModel(
    val externalId: String = "", val name: String = "", val category: String = "",
    val area: String = "", val instructions: String = "", val imageUrl: String = "",
    val ingredients: List<IngredientModel> = emptyList(), val youtubeUrl: String? = null
) {
    fun toEntity() = ExternalRecipeEntity(
        externalId = externalId, name = name, category = category, area = area,
        instructions = instructions, imageUrl = imageUrl,
        ingredients = ingredients.map { IngredientEntity(it.name, it.amount, it.unit) },
        youtubeUrl = youtubeUrl
    )
}

data class CreateRecipeRequest(
    val name: String, val description: String, val category: String,
    val imageUrl: String, val prepTimeMinutes: Int, val cookTimeMinutes: Int,
    val servings: Int, val difficulty: String,
    val ingredients: List<IngredientModel>, val steps: List<RecipeStepModel>,
    val tags: List<String>
)

data class ImportExternalRecipeRequest(
    val externalId: String, val name: String, val category: String,
    val area: String, val instructions: String, val imageUrl: String,
    val ingredients: List<IngredientModel>, val youtubeUrl: String?
)