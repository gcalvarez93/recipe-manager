// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/domain/usecase/ToggleFavoriteUseCase.kt
package com.castrodev.recipemanager.features.recipes.domain.usecase
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository
import javax.inject.Inject
class ToggleFavoriteUseCase @Inject constructor(private val repository: RecipeRepository) {
    suspend operator fun invoke(id: String) = repository.toggleFavorite(id)
}