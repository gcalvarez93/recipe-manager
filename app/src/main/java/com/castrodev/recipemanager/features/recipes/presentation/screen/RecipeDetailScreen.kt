// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/presentation/screen/RecipeDetailScreen.kt
package com.castrodev.recipemanager.features.recipes.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.features.recipes.presentation.components.RecipeFormSheet
import com.castrodev.recipemanager.features.recipes.presentation.viewmodel.RecipeDetailState
import com.castrodev.recipemanager.features.recipes.presentation.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    viewModel: RecipeViewModel,
    onNavigateBack: () -> Unit
) {
    val state         by viewModel.detailState.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) { viewModel.loadRecipeById(recipeId) }
    LaunchedEffect(actionSuccess) {
        if (actionSuccess) { showEditSheet = false; viewModel.resetActionSuccess(); viewModel.loadRecipeById(recipeId) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = {
                    if (state is RecipeDetailState.Success) {
                        IconButton(onClick = { showEditSheet = true }) { Icon(Icons.Default.Edit, null) }
                        IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is RecipeDetailState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is RecipeDetailState.Error   -> Text(s.message, modifier = Modifier.align(Alignment.Center))
                is RecipeDetailState.Success -> {
                    val recipe = s.recipe
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        if (recipe.imageUrl.isNotBlank()) {
                            AsyncImage(model = recipe.imageUrl, contentDescription = null, contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)))
                        }
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(recipe.name, style = MaterialTheme.typography.headlineMedium)
                            if (recipe.category.isNotBlank()) {
                                Text(recipe.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Spacer(Modifier.width(4.dp))
                                Text("${recipe.prepTimeMinutes + recipe.cookTimeMinutes} min · ${recipe.servings} ${stringResource(R.string.servings)}",
                                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                            if (recipe.description.isNotBlank()) {
                                Spacer(Modifier.height(12.dp))
                                Text(recipe.description, style = MaterialTheme.typography.bodyMedium)
                            }
                            if (recipe.ingredients.isNotEmpty()) {
                                Spacer(Modifier.height(16.dp))
                                Text(stringResource(R.string.ingredients), style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(8.dp))
                                recipe.ingredients.forEach { ingredient ->
                                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                        Text("• ", color = MaterialTheme.colorScheme.primary)
                                        Text("${ingredient.amount} ${ingredient.unit} ${ingredient.name}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                            if (recipe.steps.isNotEmpty()) {
                                Spacer(Modifier.height(16.dp))
                                Text(stringResource(R.string.steps), style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(8.dp))
                                recipe.steps.sortedBy { it.order }.forEach { step ->
                                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text("${step.order}. ", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleSmall)
                                        Text(step.description, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                            Spacer(Modifier.height(32.dp))
                        }
                    }

                    if (showEditSheet) {
                        RecipeFormSheet(recipe = recipe, onSave = { viewModel.update(it) }, onDismiss = { showEditSheet = false })
                    }
                }
                else -> Unit
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_recipe)) },
            text  = { Text(stringResource(R.string.delete_recipe_confirm)) },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(recipeId); showDeleteDialog = false; onNavigateBack() }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}