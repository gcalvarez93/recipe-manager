// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/presentation/screen/MealPlanScreen.kt
package com.castrodev.recipemanager.features.mealplan.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.features.mealplan.presentation.components.MealPlanDayCard
import com.castrodev.recipemanager.features.mealplan.presentation.viewmodel.MealPlanState
import com.castrodev.recipemanager.features.mealplan.presentation.viewmodel.MealPlanViewModel
import com.castrodev.recipemanager.features.recipes.presentation.viewmodel.RecipeViewModel
import com.castrodev.recipemanager.features.recipes.presentation.viewmodel.RecipesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    mealPlanViewModel: MealPlanViewModel = hiltViewModel(),
    recipeViewModel: RecipeViewModel
) {
    val state        by mealPlanViewModel.state.collectAsState()
    val actionError  by mealPlanViewModel.actionError.collectAsState()
    val recipesState by recipeViewModel.recipesState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources           = LocalResources.current

    LaunchedEffect(actionError) {
        actionError?.let { res ->
            snackbarHostState.showSnackbar(resources.getString(res))
            mealPlanViewModel.clearActionError()
        }
    }

    // day key → label
    val days = mapOf(
        "Monday"    to stringResource(R.string.monday),
        "Tuesday"   to stringResource(R.string.tuesday),
        "Wednesday" to stringResource(R.string.wednesday),
        "Thursday"  to stringResource(R.string.thursday),
        "Friday"    to stringResource(R.string.friday),
        "Saturday"  to stringResource(R.string.saturday),
        "Sunday"    to stringResource(R.string.sunday)
    )
    val mealTypes = mapOf(
        "Breakfast" to stringResource(R.string.breakfast),
        "Lunch"     to stringResource(R.string.lunch),
        "Dinner"    to stringResource(R.string.dinner)
    )

    // (dayKey, mealKey) pending selection
    var showPickerFor by remember { mutableStateOf<Pair<String, String>?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_mealplan)) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Week selector
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { mealPlanViewModel.previousWeek() }) { Icon(Icons.Default.ChevronLeft, null) }
                Text(
                    text  = "${stringResource(R.string.week)} ${mealPlanViewModel.selectedWeek} · ${mealPlanViewModel.selectedYear}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { mealPlanViewModel.nextWeek() }) { Icon(Icons.Default.ChevronRight, null) }
            }

            when (val s = state) {
                is MealPlanState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is MealPlanState.Error   -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(s.messageRes)) }
                is MealPlanState.Success -> {
                    val entries = s.plan?.entries ?: emptyList()
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(days.keys.toList()) { dayKey ->
                            val dayEntries = entries.filter { it.dayOfWeek == dayKey }
                            MealPlanDayCard(
                                day           = days[dayKey] ?: dayKey,
                                mealTypes     = mealTypes,
                                entries       = dayEntries,
                                onAddEntry    = { mealKey -> showPickerFor = Pair(dayKey, mealKey) },
                                onRemoveEntry = { mealKey -> mealPlanViewModel.removeEntry(dayKey, mealKey) }
                            )
                        }
                    }
                }
            }
        }
    }

    showPickerFor?.let { (dayKey, mealKey) ->
        val recipes = (recipesState as? RecipesState.Success)?.recipes ?: emptyList()
        AlertDialog(
            onDismissRequest = { showPickerFor = null },
            title = { Text(stringResource(R.string.select_recipe)) },
            text = {
                LazyColumn {
                    items(recipes) { recipe ->
                        TextButton(
                            onClick = {
                                mealPlanViewModel.addEntry(dayKey, mealKey, recipe.id, recipe.name)
                                showPickerFor = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(recipe.name, style = MaterialTheme.typography.bodyMedium) }
                    }
                }
            },
            confirmButton  = {},
            dismissButton  = { TextButton(onClick = { showPickerFor = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}