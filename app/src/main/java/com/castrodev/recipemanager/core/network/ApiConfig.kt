// Path: app/src/main/java/com/castrodev/recipemanager/core/network/ApiConfig.kt
package com.castrodev.recipemanager.core.network

object ApiConfig {
    const val BASE_URL = "https://api.castrodev.com/"

    object Recipes {
        const val BASE     = "api/recipes"
        const val EXTERNAL = "api/recipes/external"
        const val MEALPLAN = "api/recipes/mealplan"
        const val SHOPPING = "api/recipes/shopping"
    }
}