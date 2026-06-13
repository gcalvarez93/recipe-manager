// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/presentation/screen/ExternalSearchScreen.kt
package com.castrodev.recipemanager.features.recipes.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.castrodev.recipemanager.features.recipes.domain.entity.ExternalRecipeEntity
import com.castrodev.recipemanager.features.recipes.presentation.viewmodel.ExternalSearchState
import com.castrodev.recipemanager.features.recipes.presentation.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalSearchScreen(
    viewModel: RecipeViewModel,
    onNavigateBack: () -> Unit
) {
    val state         by viewModel.externalState.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()
    var query         by remember { mutableStateOf("") }
    var importTarget  by remember { mutableStateOf<ExternalRecipeEntity?>(null) }

    LaunchedEffect(actionSuccess) {
        if (actionSuccess) { importTarget = null; viewModel.resetActionSuccess() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.search_external)) },
                navigationIcon = { IconButton(onClick = { viewModel.clearExternalSearch(); onNavigateBack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                label = { Text(stringResource(R.string.search_recipe)) },
                trailingIcon = { IconButton(onClick = { if (query.isNotBlank()) viewModel.searchExternal(query) }) { Icon(Icons.Default.Search, null) } },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            when (val s = state) {
                is ExternalSearchState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is ExternalSearchState.Error   -> Text(s.message, color = MaterialTheme.colorScheme.error)
                is ExternalSearchState.Success -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(s.recipes, key = { it.externalId }) { recipe ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { importTarget = recipe },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (recipe.imageUrl.isNotBlank()) {
                                        AsyncImage(model = recipe.imageUrl, contentDescription = null, contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)))
                                        Spacer(Modifier.width(12.dp))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(recipe.name, style = MaterialTheme.typography.titleSmall)
                                        Text("${recipe.category} · ${recipe.area}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    importTarget?.let { recipe ->
        AlertDialog(
            onDismissRequest = { importTarget = null },
            title = { Text(stringResource(R.string.import_recipe)) },
            text  = { Text(stringResource(R.string.import_recipe_confirm, recipe.name)) },
            confirmButton = {
                TextButton(onClick = { viewModel.importExternal(recipe) }) { Text(stringResource(R.string.import_label)) }
            },
            dismissButton = { TextButton(onClick = { importTarget = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}