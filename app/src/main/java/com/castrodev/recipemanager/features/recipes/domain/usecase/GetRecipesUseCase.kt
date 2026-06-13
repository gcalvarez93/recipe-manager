// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/domain/usecase/GetRecipesUseCase.kt
package com.castrodev.recipemanager.features.recipes.domain.usecase
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository
class GetRecipesUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(category: String? = null, favoritesOnly: Boolean? = null) = repository.getRecipes(category, favoritesOnly)
}