// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/domain/usecase/SearchExternalRecipesUseCase.kt
package com.castrodev.recipemanager.features.recipes.domain.usecase
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository
class SearchExternalRecipesUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(query: String) = repository.searchExternalRecipes(query)
}