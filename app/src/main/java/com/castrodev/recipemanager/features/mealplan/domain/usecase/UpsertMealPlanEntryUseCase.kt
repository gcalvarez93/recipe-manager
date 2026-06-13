// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/domain/usecase/UpsertMealPlanEntryUseCase.kt
package com.castrodev.recipemanager.features.mealplan.domain.usecase
import com.castrodev.recipemanager.features.mealplan.domain.repository.MealPlanRepository
class UpsertMealPlanEntryUseCase(private val repository: MealPlanRepository) {
    suspend operator fun invoke(year: Int, week: Int, dayOfWeek: String, mealType: String, recipeId: String, recipeName: String) =
        repository.upsertEntry(year, week, dayOfWeek, mealType, recipeId, recipeName)
}