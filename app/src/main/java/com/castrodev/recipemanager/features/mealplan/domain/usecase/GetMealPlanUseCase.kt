// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/domain/usecase/GetMealPlanUseCase.kt
package com.castrodev.recipemanager.features.mealplan.domain.usecase
import com.castrodev.recipemanager.features.mealplan.domain.repository.MealPlanRepository
class GetMealPlanUseCase(private val repository: MealPlanRepository) {
    suspend operator fun invoke(year: Int, week: Int) = repository.getMealPlan(year, week)
}