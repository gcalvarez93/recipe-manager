// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/domain/usecase/CreateRecipeUseCase.kt
package com.castrodev.recipemanager.features.recipes.domain.usecase
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository
import javax.inject.Inject
class CreateRecipeUseCase @Inject constructor(private val repository: RecipeRepository) {
    suspend operator fun invoke(recipe: RecipeEntity): Result<String> {
        if (recipe.name.isBlank()) return Result.failure(IllegalArgumentException("Name cannot be empty"))
        return repository.createRecipe(recipe)
    }
}