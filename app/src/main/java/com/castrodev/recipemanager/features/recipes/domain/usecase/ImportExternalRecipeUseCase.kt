// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/domain/usecase/ImportExternalRecipeUseCase.kt
package com.castrodev.recipemanager.features.recipes.domain.usecase
import com.castrodev.recipemanager.features.recipes.domain.entity.ExternalRecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository
class ImportExternalRecipeUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(recipe: ExternalRecipeEntity) = repository.importExternalRecipe(recipe)
}