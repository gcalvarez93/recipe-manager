// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/domain/entity/MealPlanEntity.kt
package com.castrodev.recipemanager.features.mealplan.domain.entity

data class MealPlanEntity(
    val id: String,
    val year: Int,
    val week: Int,
    val entries: List<MealPlanEntryEntity>,
    val createdAt: String
)

data class MealPlanEntryEntity(
    val dayOfWeek: String,
    val mealType: String,
    val recipeId: String,
    val recipeName: String
)