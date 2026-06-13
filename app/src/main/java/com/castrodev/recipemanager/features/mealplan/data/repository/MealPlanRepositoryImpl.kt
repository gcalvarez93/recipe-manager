// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/data/repository/MealPlanRepositoryImpl.kt
package com.castrodev.recipemanager.features.mealplan.data.repository

import com.castrodev.recipemanager.features.mealplan.data.datasource.MealPlanRemoteDataSource
import com.castrodev.recipemanager.features.mealplan.domain.entity.MealPlanEntity
import com.castrodev.recipemanager.features.mealplan.domain.repository.MealPlanRepository

class MealPlanRepositoryImpl(
    private val dataSource: MealPlanRemoteDataSource = MealPlanRemoteDataSource()
) : MealPlanRepository {
    override suspend fun getMealPlan(year: Int, week: Int): Result<MealPlanEntity?> =
        runCatching { dataSource.getMealPlan(year, week)?.toEntity() }

    override suspend fun upsertEntry(year: Int, week: Int, dayOfWeek: String, mealType: String, recipeId: String, recipeName: String): Result<String> =
        runCatching { dataSource.upsertEntry(year, week, dayOfWeek, mealType, recipeId, recipeName) }

    override suspend fun deleteEntry(year: Int, week: Int, dayOfWeek: String, mealType: String): Result<Unit> =
        runCatching { dataSource.deleteEntry(year, week, dayOfWeek, mealType) }
}