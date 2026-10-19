// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/domain/usecase/DeleteMealPlanEntryUseCase.kt
package com.castrodev.recipemanager.features.mealplan.domain.usecase
import com.castrodev.recipemanager.features.mealplan.domain.repository.MealPlanRepository
import javax.inject.Inject
class DeleteMealPlanEntryUseCase @Inject constructor(private val repository: MealPlanRepository) {
    suspend operator fun invoke(year: Int, week: Int, dayOfWeek: String, mealType: String) =
        repository.deleteEntry(year, week, dayOfWeek, mealType)
}