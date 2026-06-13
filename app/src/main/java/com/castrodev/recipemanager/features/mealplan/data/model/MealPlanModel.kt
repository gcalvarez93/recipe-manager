// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/data/model/MealPlanModel.kt
package com.castrodev.recipemanager.features.mealplan.data.model

import com.castrodev.recipemanager.features.mealplan.domain.entity.MealPlanEntity
import com.castrodev.recipemanager.features.mealplan.domain.entity.MealPlanEntryEntity

data class MealPlanModel(
    val id: String = "",
    val year: Int = 0,
    val week: Int = 0,
    val entries: List<MealPlanEntryModel> = emptyList(),
    val createdAt: String = ""
) {
    fun toEntity() = MealPlanEntity(
        id = id, year = year, week = week,
        entries = entries.map { MealPlanEntryEntity(it.dayOfWeek, it.mealType, it.recipeId, it.recipeName) },
        createdAt = createdAt
    )
}

data class MealPlanEntryModel(
    val dayOfWeek: String = "",
    val mealType: String = "",
    val recipeId: String = "",
    val recipeName: String = ""
)

data class UpsertMealPlanEntryRequest(
    val dayOfWeek: String,
    val mealType: String,
    val recipeId: String,
    val recipeName: String
)