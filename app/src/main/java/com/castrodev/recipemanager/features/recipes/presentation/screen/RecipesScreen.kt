// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/presentation/screen/RecipesScreen.kt
package com.castrodev.recipemanager.features.recipes.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.features.recipes.presentation.components.RecipeCard
import com.castrodev.recipemanager.features.recipes.presentation.components.RecipeFormSheet
import com.castrodev.recipemanager.features.recipes.presentation.viewmodel.RecipeViewModel
import com.castrodev.recipemanager.features.recipes.presentation.viewmodel.RecipesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    viewModel: RecipeViewModel = viewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val state          by viewModel.recipesState.collectAsState()
    val actionSuccess  by viewModel.actionSuccess.collectAsState()
    var showAddSheet   by remember { mutableStateOf(false) }

    LaunchedEffect(actionSuccess) {
        if (actionSuccess) { showAddSheet = false; viewModel.resetActionSuccess() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tab_recipes)) },
                actions = {
                    IconButton(onClick = onNavigateToSearch) { Icon(Icons.Default.Search, null) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, null) }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is RecipesState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is RecipesState.Error   -> Text(s.message, modifier = Modifier.align(Alignment.Center))
                is RecipesState.Success -> {
                    if (s.recipes.isEmpty()) {
                        Text(stringResource(R.string.no_recipes), modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(s.recipes, key = { it.id }) { recipe ->
                                RecipeCard(
                                    recipe = recipe,
                                    onClick = { onNavigateToDetail(recipe.id) },
                                    onToggleFavorite = { viewModel.toggleFavorite(recipe.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        RecipeFormSheet(
            onSave    = { viewModel.create(it) },
            onDismiss = { showAddSheet = false }
        )
    }
}