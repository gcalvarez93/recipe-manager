// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/domain/repository/RecipeRepository.kt
package com.castrodev.recipemanager.features.recipes.domain.repository

import com.castrodev.recipemanager.features.recipes.domain.entity.ExternalRecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeEntity

interface RecipeRepository {
    suspend fun getRecipes(category: String? = null, favoritesOnly: Boolean? = null): Result<List<RecipeEntity>>
    suspend fun getRecipeById(id: String): Result<RecipeEntity>
    suspend fun createRecipe(recipe: RecipeEntity): Result<String>
    suspend fun updateRecipe(recipe: RecipeEntity): Result<Unit>
    suspend fun deleteRecipe(id: String): Result<Unit>
    suspend fun toggleFavorite(id: String): Result<Unit>
    suspend fun searchExternalRecipes(query: String): Result<List<ExternalRecipeEntity>>
    suspend fun importExternalRecipe(recipe: ExternalRecipeEntity): Result<String>
}