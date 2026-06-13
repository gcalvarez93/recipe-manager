// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/data/datasource/MealPlanRemoteDataSource.kt
package com.castrodev.recipemanager.features.mealplan.data.datasource

import com.castrodev.recipemanager.core.network.FirebaseTokenProvider
import com.castrodev.recipemanager.core.network.NetworkClient
import com.castrodev.recipemanager.features.mealplan.data.model.MealPlanModel
import com.castrodev.recipemanager.features.mealplan.data.model.UpsertMealPlanEntryRequest

class MealPlanRemoteDataSource {
    private val api = NetworkClient.buildRetrofit(FirebaseTokenProvider()).create(MealPlanApiService::class.java)

    suspend fun getMealPlan(year: Int, week: Int): MealPlanModel? {
        return try {
            api.getMealPlan(year, week)
        } catch (e: Exception) {
            // Body vacío o null → no hay plan para esta semana
            null
        }
    }

    suspend fun upsertEntry(year: Int, week: Int, dayOfWeek: String, mealType: String, recipeId: String, recipeName: String) =
        api.upsertEntry(year, week, UpsertMealPlanEntryRequest(dayOfWeek, mealType, recipeId, recipeName))

    suspend fun deleteEntry(year: Int, week: Int, dayOfWeek: String, mealType: String) =
        api.deleteEntry(year, week, dayOfWeek, mealType)
}