// Path: app/src/test/java/com/castrodev/recipemanager/fakes/FakeRecipeRepository.kt
package com.castrodev.recipemanager.fakes

import com.castrodev.recipemanager.features.recipes.domain.entity.ExternalRecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository

fun testRecipe(id: String = "r1", name: String = "Tortilla de patatas") = RecipeEntity(
    id = id, name = name, description = "Clásica", category = "Cena", imageUrl = "",
    prepTimeMinutes = 10, cookTimeMinutes = 20, servings = 4, difficulty = "easy",
    ingredients = emptyList(), steps = emptyList(), tags = emptyList(),
    isFavorite = false, externalId = null, isImported = false, createdAt = "2026-01-01"
)

/** Repositorio de recetas en memoria con resultados configurables. */
class FakeRecipeRepository : RecipeRepository {
    var recipesResult: Result<List<RecipeEntity>> = Result.success(emptyList())
    var createResult: Result<String> = Result.success("new-id")
    var toggleResult: Result<Unit> = Result.success(Unit)

    var getRecipesCalls = 0
    var lastCategory: String? = null
    var lastFavoritesOnly: Boolean? = null
    var toggledId: String? = null
    var created: RecipeEntity? = null

    override suspend fun getRecipes(category: String?, favoritesOnly: Boolean?): Result<List<RecipeEntity>> {
        getRecipesCalls++
        lastCategory = category
        lastFavoritesOnly = favoritesOnly
        return recipesResult
    }

    override suspend fun getRecipeById(id: String): Result<RecipeEntity> =
        recipesResult.map { list -> list.first { it.id == id } }

    override suspend fun createRecipe(recipe: RecipeEntity): Result<String> {
        created = recipe
        return createResult
    }

    override suspend fun updateRecipe(recipe: RecipeEntity): Result<Unit> = Result.success(Unit)

    override suspend fun deleteRecipe(id: String): Result<Unit> = Result.success(Unit)

    override suspend fun toggleFavorite(id: String): Result<Unit> {
        toggledId = id
        return toggleResult
    }

    override suspend fun searchExternalRecipes(query: String): Result<List<ExternalRecipeEntity>> =
        Result.success(emptyList())

    override suspend fun importExternalRecipe(recipe: ExternalRecipeEntity): Result<String> =
        Result.success("imported-id")
}