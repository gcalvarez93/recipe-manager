// Path: app/src/main/java/com/castrodev/recipemanager/navigation/MainScreen.kt
package com.castrodev.recipemanager.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.features.mealplan.presentation.screen.MealPlanScreen
import com.castrodev.recipemanager.features.mealplan.presentation.viewmodel.MealPlanViewModel
import com.castrodev.recipemanager.features.recipes.presentation.screen.ExternalSearchScreen
import com.castrodev.recipemanager.features.recipes.presentation.screen.RecipeDetailScreen
import com.castrodev.recipemanager.features.recipes.presentation.screen.RecipesScreen
import com.castrodev.recipemanager.features.recipes.presentation.viewmodel.RecipeViewModel
import com.castrodev.recipemanager.features.shopping.presentation.screen.ShoppingScreen
import com.castrodev.recipemanager.features.shopping.presentation.viewmodel.ShoppingViewModel

sealed class BottomTab(val route: String, val labelRes: Int, val icon: ImageVector) {
    object Recipes  : BottomTab("recipes",  R.string.tab_recipes,  Icons.Default.Restaurant)
    object MealPlan : BottomTab("mealplan", R.string.tab_mealplan, Icons.Default.CalendarMonth)
    object Shopping : BottomTab("shopping", R.string.tab_shopping, Icons.Default.ShoppingCart)
    object Profile  : BottomTab("profile",  R.string.tab_profile,  Icons.Default.Person)
}

sealed class InnerScreen(val route: String) {
    object RecipeDetail   : InnerScreen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }
    object ExternalSearch : InnerScreen("external_search")
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController            = rememberNavController()
    val recipeViewModel: RecipeViewModel       = viewModel()
    val mealPlanViewModel: MealPlanViewModel   = viewModel()
    val shoppingViewModel: ShoppingViewModel   = viewModel()
    val tabs = listOf(BottomTab.Recipes, BottomTab.MealPlan, BottomTab.Shopping, BottomTab.Profile)
    val tabRoutes = tabs.map { it.route }

    Scaffold(
        bottomBar = {
            val navBackStackEntry  by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = tabRoutes.any { route ->
                currentDestination?.hierarchy?.any { it.route == route } == true
            }
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick  = {
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
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = BottomTab.Recipes.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(BottomTab.Recipes.route) {
                RecipesScreen(
                    viewModel          = recipeViewModel,
                    onNavigateToDetail = { id -> navController.navigate(InnerScreen.RecipeDetail.createRoute(id)) },
                    onNavigateToSearch = { navController.navigate(InnerScreen.ExternalSearch.route) }
                )
            }
            composable(InnerScreen.RecipeDetail.route) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
                RecipeDetailScreen(
                    recipeId       = recipeId,
                    viewModel      = recipeViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(InnerScreen.ExternalSearch.route) {
                ExternalSearchScreen(
                    viewModel      = recipeViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(BottomTab.MealPlan.route) {
                MealPlanScreen(
                    mealPlanViewModel = mealPlanViewModel,
                    recipeViewModel   = recipeViewModel
                )
            }
            composable(BottomTab.Shopping.route) {
                ShoppingScreen(viewModel = shoppingViewModel)
            }
            composable(BottomTab.Profile.route) { /* ProfileScreen — próximamente */ }
        }
    }
}