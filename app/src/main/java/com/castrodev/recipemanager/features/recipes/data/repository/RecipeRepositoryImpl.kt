// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/data/repository/RecipeRepositoryImpl.kt
package com.castrodev.recipemanager.features.recipes.data.repository

import com.castrodev.recipemanager.core.util.safeCall
import com.castrodev.recipemanager.features.recipes.data.datasource.RecipeRemoteDataSource
import com.castrodev.recipemanager.features.recipes.domain.entity.ExternalRecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val dataSource: RecipeRemoteDataSource
) : RecipeRepository {
    override suspend fun getRecipes(category: String?, favoritesOnly: Boolean?) = safeCall("RecipeRepositoryImpl.getRecipes") { dataSource.getRecipes(category, favoritesOnly).map { it.toEntity() } }
    override suspend fun getRecipeById(id: String) = safeCall("RecipeRepositoryImpl.getRecipeById") { dataSource.getRecipeById(id).toEntity() }
    override suspend fun createRecipe(recipe: RecipeEntity) = safeCall("RecipeRepositoryImpl.createRecipe") { dataSource.createRecipe(recipe) }
    override suspend fun updateRecipe(recipe: RecipeEntity) = safeCall("RecipeRepositoryImpl.updateRecipe") { dataSource.updateRecipe(recipe) }
    override suspend fun deleteRecipe(id: String) = safeCall("RecipeRepositoryImpl.deleteRecipe") { dataSource.deleteRecipe(id) }
    override suspend fun toggleFavorite(id: String) = safeCall("RecipeRepositoryImpl.toggleFavorite") { dataSource.toggleFavorite(id) }
    override suspend fun searchExternalRecipes(query: String) = safeCall("RecipeRepositoryImpl.searchExternalRecipes") { dataSource.searchExternalRecipes(query).map { it.toEntity() } }
    override suspend fun importExternalRecipe(recipe: ExternalRecipeEntity) = safeCall("RecipeRepositoryImpl.importExternalRecipe") { dataSource.importExternalRecipe(recipe) }
}