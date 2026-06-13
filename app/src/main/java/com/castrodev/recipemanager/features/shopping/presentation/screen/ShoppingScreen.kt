// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/presentation/screen/ShoppingScreen.kt
package com.castrodev.recipemanager.features.shopping.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.features.shopping.presentation.components.ShoppingItemRow
import com.castrodev.recipemanager.features.shopping.presentation.viewmodel.ShoppingState
import com.castrodev.recipemanager.features.shopping.presentation.viewmodel.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(viewModel: ShoppingViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tab_shopping)) },
                actions = {
                    IconButton(onClick = { viewModel.generate() }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.generate_list))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Week selector
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.previousWeek() }) { Icon(Icons.Default.ChevronLeft, null) }
                Text(
                    text  = "${stringResource(R.string.week)} ${viewModel.selectedWeek} · ${viewModel.selectedYear}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { viewModel.nextWeek() }) { Icon(Icons.Default.ChevronRight, null) }
            }

            when (val s = state) {
                is ShoppingState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is ShoppingState.Error   -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(s.message) }
                is ShoppingState.Empty   -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.no_shopping_list), style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.generate() }) {
                                Text(stringResource(R.string.generate_list))
                            }
                        }
                    }
                }
                is ShoppingState.Success -> {
                    val pending  = s.list.items.filter { !it.isChecked }
                    val checked  = s.list.items.filter { it.isChecked }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        if (pending.isNotEmpty()) {
                            item {
                                Text(
                                    text     = stringResource(R.string.pending),
                                    style    = MaterialTheme.typography.titleSmall,
                                    color    = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            items(pending, key = { it.name }) { item ->
                                ShoppingItemRow(item = item, onToggle = { viewModel.toggle(item.name) })
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                        if (checked.isNotEmpty()) {
                            item {
                                Text(
                                    text     = stringResource(R.string.completed),
                                    style    = MaterialTheme.typography.titleSmall,
                                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            items(checked, key = { it.name }) { item ->
                                ShoppingItemRow(item = item, onToggle = { viewModel.toggle(item.name) })
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}