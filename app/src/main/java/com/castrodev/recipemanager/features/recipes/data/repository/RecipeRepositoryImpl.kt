// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/data/repository/RecipeRepositoryImpl.kt
package com.castrodev.recipemanager.features.recipes.data.repository

import com.castrodev.recipemanager.features.recipes.data.datasource.RecipeRemoteDataSource
import com.castrodev.recipemanager.features.recipes.domain.entity.ExternalRecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository

class RecipeRepositoryImpl(
    private val dataSource: RecipeRemoteDataSource = RecipeRemoteDataSource()
) : RecipeRepository {
    override suspend fun getRecipes(category: String?, favoritesOnly: Boolean?) = runCatching { dataSource.getRecipes(category, favoritesOnly).map { it.toEntity() } }
    override suspend fun getRecipeById(id: String) = runCatching { dataSource.getRecipeById(id).toEntity() }
    override suspend fun createRecipe(recipe: RecipeEntity) = runCatching { dataSource.createRecipe(recipe) }
    override suspend fun updateRecipe(recipe: RecipeEntity) = runCatching { dataSource.updateRecipe(recipe) }
    override suspend fun deleteRecipe(id: String) = runCatching { dataSource.deleteRecipe(id) }
    override suspend fun toggleFavorite(id: String) = runCatching { dataSource.toggleFavorite(id) }
    override suspend fun searchExternalRecipes(query: String) = runCatching { dataSource.searchExternalRecipes(query).map { it.toEntity() } }
    override suspend fun importExternalRecipe(recipe: ExternalRecipeEntity) = runCatching { dataSource.importExternalRecipe(recipe) }
}