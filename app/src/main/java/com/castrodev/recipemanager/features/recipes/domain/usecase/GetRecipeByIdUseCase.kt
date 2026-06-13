// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/domain/usecase/GetRecipeByIdUseCase.kt
package com.castrodev.recipemanager.features.recipes.domain.usecase
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository
class GetRecipeByIdUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(id: String) = repository.getRecipeById(id)
}