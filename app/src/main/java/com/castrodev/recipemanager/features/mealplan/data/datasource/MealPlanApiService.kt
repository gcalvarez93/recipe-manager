// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/data/datasource/MealPlanApiService.kt
package com.castrodev.recipemanager.features.mealplan.data.datasource

import com.castrodev.recipemanager.features.mealplan.data.model.MealPlanModel
import com.castrodev.recipemanager.features.mealplan.data.model.UpsertMealPlanEntryRequest
import retrofit2.http.*

interface MealPlanApiService {
    @GET("api/recipes/mealplan/{year}/{week}")
    suspend fun getMealPlan(@Path("year") year: Int, @Path("week") week: Int): MealPlanModel?

    @PUT("api/recipes/mealplan/{year}/{week}")
    suspend fun upsertEntry(@Path("year") year: Int, @Path("week") week: Int, @Body request: UpsertMealPlanEntryRequest): String

    @DELETE("api/recipes/mealplan/{year}/{week}/{dayOfWeek}/{mealType}")
    suspend fun deleteEntry(@Path("year") year: Int, @Path("week") week: Int, @Path("dayOfWeek") dayOfWeek: String, @Path("mealType") mealType: String)
}