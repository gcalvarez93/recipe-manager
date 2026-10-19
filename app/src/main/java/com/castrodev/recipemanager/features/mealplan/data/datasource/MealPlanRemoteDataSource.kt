// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/data/datasource/MealPlanRemoteDataSource.kt
package com.castrodev.recipemanager.features.mealplan.data.datasource

import com.castrodev.recipemanager.features.mealplan.data.model.MealPlanModel
import com.castrodev.recipemanager.features.mealplan.data.model.UpsertMealPlanEntryRequest
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import javax.inject.Inject

class MealPlanRemoteDataSource @Inject constructor(
    private val api: MealPlanApiService
) {

    suspend fun getMealPlan(year: Int, week: Int): MealPlanModel? {
        return try {
            api.getMealPlan(year, week)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Body vacío o null → no hay plan para esta semana
            Timber.w(e, "MealPlanRemoteDataSource.getMealPlan: sin plan para %d/%d", year, week)
            null
        }
    }

    suspend fun upsertEntry(year: Int, week: Int, dayOfWeek: String, mealType: String, recipeId: String, recipeName: String) =
        api.upsertEntry(year, week, UpsertMealPlanEntryRequest(dayOfWeek, mealType, recipeId, recipeName))

    suspend fun deleteEntry(year: Int, week: Int, dayOfWeek: String, mealType: String) =
        api.deleteEntry(year, week, dayOfWeek, mealType)
}