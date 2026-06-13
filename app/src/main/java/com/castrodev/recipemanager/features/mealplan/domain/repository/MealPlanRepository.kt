// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/domain/repository/MealPlanRepository.kt
package com.castrodev.recipemanager.features.mealplan.domain.repository

import com.castrodev.recipemanager.features.mealplan.domain.entity.MealPlanEntity

interface MealPlanRepository {
    suspend fun getMealPlan(year: Int, week: Int): Result<MealPlanEntity?>
    suspend fun upsertEntry(year: Int, week: Int, dayOfWeek: String, mealType: String, recipeId: String, recipeName: String): Result<String>
    suspend fun deleteEntry(year: Int, week: Int, dayOfWeek: String, mealType: String): Result<Unit>
}