// Path: app/src/main/java/com/castrodev/recipemanager/navigation/Screen.kt
package com.castrodev.recipemanager.navigation

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object Main     : Screen("main")
}