// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/presentation/components/MealPlanDayCard.kt
package com.castrodev.recipemanager.features.mealplan.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.castrodev.recipemanager.features.mealplan.domain.entity.MealPlanEntryEntity

@Composable
fun MealPlanDayCard(
    day: String,
    mealTypes: Map<String, String>,   // key → label
    entries: List<MealPlanEntryEntity>,
    onAddEntry: (mealKey: String) -> Unit,
    onRemoveEntry: (mealKey: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(day, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            mealTypes.forEach { (mealKey, mealLabel) ->
                val entry = entries.firstOrNull { it.mealType == mealKey }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = mealLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.width(80.dp)
                    )
                    if (entry != null) {
                        Text(entry.recipeName, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onRemoveEntry(mealKey) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { onAddEntry(mealKey) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}