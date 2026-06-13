// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/domain/usecase/UpdateRecipeUseCase.kt
package com.castrodev.recipemanager.features.recipes.domain.usecase
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository
class UpdateRecipeUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(recipe: RecipeEntity) = repository.updateRecipe(recipe)
}