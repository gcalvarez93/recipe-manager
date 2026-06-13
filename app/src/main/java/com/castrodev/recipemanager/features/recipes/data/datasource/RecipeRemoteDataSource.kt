// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/data/datasource/RecipeRemoteDataSource.kt
package com.castrodev.recipemanager.features.recipes.data.datasource

import com.castrodev.recipemanager.core.network.FirebaseTokenProvider
import com.castrodev.recipemanager.core.network.NetworkClient
import com.castrodev.recipemanager.features.recipes.data.model.*
import com.castrodev.recipemanager.features.recipes.domain.entity.ExternalRecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeEntity

class RecipeRemoteDataSource {
    private val api = NetworkClient.buildRetrofit(FirebaseTokenProvider()).create(RecipeApiService::class.java)

    suspend fun getRecipes(category: String?, favoritesOnly: Boolean?) = api.getRecipes(category, favoritesOnly)
    suspend fun getRecipeById(id: String) = api.getRecipeById(id)
    suspend fun createRecipe(recipe: RecipeEntity) = api.createRecipe(
        CreateRecipeRequest(recipe.name, recipe.description, recipe.category, recipe.imageUrl,
            recipe.prepTimeMinutes, recipe.cookTimeMinutes, recipe.servings, recipe.difficulty,
            recipe.ingredients.map { IngredientModel(it.name, it.amount, it.unit) },
            recipe.steps.map { RecipeStepModel(it.order, it.description) }, recipe.tags)
    )
    suspend fun updateRecipe(recipe: RecipeEntity) = api.updateRecipe(recipe.id,
        CreateRecipeRequest(recipe.name, recipe.description, recipe.category, recipe.imageUrl,
            recipe.prepTimeMinutes, recipe.cookTimeMinutes, recipe.servings, recipe.difficulty,
            recipe.ingredients.map { IngredientModel(it.name, it.amount, it.unit) },
            recipe.steps.map { RecipeStepModel(it.order, it.description) }, recipe.tags)
    )
    suspend fun deleteRecipe(id: String) = api.deleteRecipe(id)
    suspend fun toggleFavorite(id: String) = api.toggleFavorite(id)
    suspend fun searchExternalRecipes(query: String) = api.searchExternalRecipes(query)
    suspend fun importExternalRecipe(recipe: ExternalRecipeEntity) = api.importExternalRecipe(
        ImportExternalRecipeRequest(recipe.externalId, recipe.name, recipe.category, recipe.area,
            recipe.instructions, recipe.imageUrl,
            recipe.ingredients.map { IngredientModel(it.name, it.amount, it.unit) }, recipe.youtubeUrl)
    )
}