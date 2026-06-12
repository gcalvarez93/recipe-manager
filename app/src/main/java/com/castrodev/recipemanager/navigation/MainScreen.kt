// Path: app/src/main/java/com/castrodev/recipemanager/navigation/MainScreen.kt
package com.castrodev.recipemanager.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.castrodev.recipemanager.R

sealed class BottomTab(val route: String, val labelRes: Int, val icon: ImageVector) {
    object Recipes  : BottomTab("recipes",  R.string.tab_recipes,  Icons.Default.Restaurant)
    object MealPlan : BottomTab("mealplan", R.string.tab_mealplan, Icons.Default.CalendarMonth)
    object Shopping : BottomTab("shopping", R.string.tab_shopping, Icons.Default.ShoppingCart)
    object Profile  : BottomTab("profile",  R.string.tab_profile,  Icons.Default.Person)
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val tabs = listOf(BottomTab.Recipes, BottomTab.MealPlan, BottomTab.Shopping, BottomTab.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon  = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = BottomTab.Recipes.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(BottomTab.Recipes.route)  { /* RecipesScreen - se añade después */ }
            composable(BottomTab.MealPlan.route) { /* MealPlanScreen - se añade después */ }
            composable(BottomTab.Shopping.route) { /* ShoppingScreen - se añade después */ }
            composable(BottomTab.Profile.route)  { /* ProfileScreen - se añade después */ }
        }
    }
}