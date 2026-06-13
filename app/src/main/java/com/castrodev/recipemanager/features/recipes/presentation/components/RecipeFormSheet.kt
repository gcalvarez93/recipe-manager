// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/presentation/components/RecipeFormSheet.kt
package com.castrodev.recipemanager.features.recipes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.features.recipes.domain.entity.IngredientEntity
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeStepEntity

private data class IngredientForm(
    val name: String = "",
    val amount: String = "",
    val unit: String = ""
)

private data class StepForm(val description: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormSheet(
    recipe: RecipeEntity? = null,
    onSave: (RecipeEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name        by remember { mutableStateOf(recipe?.name ?: "") }
    var description by remember { mutableStateOf(recipe?.description ?: "") }
    var category    by remember { mutableStateOf(recipe?.category ?: "") }
    var imageUrl    by remember { mutableStateOf(recipe?.imageUrl ?: "") }
    var prepTime    by remember { mutableStateOf((recipe?.prepTimeMinutes ?: 0).toString()) }
    var cookTime    by remember { mutableStateOf((recipe?.cookTimeMinutes ?: 0).toString()) }
    var servings    by remember { mutableStateOf((recipe?.servings ?: 1).toString()) }

    var ingredients: List<IngredientForm> by remember {
        mutableStateOf(
            recipe?.ingredients?.map { IngredientForm(it.name, it.amount, it.unit) }
                ?: emptyList()
        )
    }
    var steps: List<StepForm> by remember {
        mutableStateOf(
            recipe?.steps?.map { StepForm(it.description) } ?: emptyList()
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text     = if (recipe == null) stringResource(R.string.add_recipe) else stringResource(R.string.edit_recipe),
                style    = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.recipe_name)) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.description)) }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text(stringResource(R.string.category)) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text(stringResource(R.string.image_url)) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = prepTime, onValueChange = { prepTime = it }, label = { Text(stringResource(R.string.prep_time)) }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = cookTime, onValueChange = { cookTime = it }, label = { Text(stringResource(R.string.cook_time)) }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = servings, onValueChange = { servings = it }, label = { Text(stringResource(R.string.servings)) }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.ingredients), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            ingredients.forEachIndexed { index, ingredient ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    OutlinedTextField(
                        value         = ingredient.name,
                        onValueChange = { new ->
                            val list = ingredients.toMutableList()
                            list[index] = ingredient.copy(name = new)
                            ingredients = list
                        },
                        label    = { Text(stringResource(R.string.name)) },
                        modifier = Modifier.weight(2f)
                    )
                    Spacer(Modifier.width(4.dp))
                    OutlinedTextField(
                        value         = ingredient.amount,
                        onValueChange = { new ->
                            val list = ingredients.toMutableList()
                            list[index] = ingredient.copy(amount = new)
                            ingredients = list
                        },
                        label    = { Text(stringResource(R.string.amount)) },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        val list = ingredients.toMutableList()
                        list.removeAt(index)
                        ingredients = list
                    }) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            TextButton(onClick = { ingredients = ingredients + listOf(IngredientForm()) }) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.add_ingredient))
            }
            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.steps), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            steps.forEachIndexed { index, step ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("${index + 1}.", modifier = Modifier.padding(end = 8.dp))
                    OutlinedTextField(
                        value         = step.description,
                        onValueChange = { new ->
                            val list = steps.toMutableList()
                            list[index] = step.copy(description = new)
                            steps = list
                        },
                        label    = { Text(stringResource(R.string.step_description)) },
                        modifier = Modifier.weight(1f),
                        minLines = 2
                    )
                    IconButton(onClick = {
                        val list = steps.toMutableList()
                        list.removeAt(index)
                        steps = list
                    }) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            TextButton(onClick = { steps = steps + listOf(StepForm()) }) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.add_step))
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(
                        RecipeEntity(
                            id              = recipe?.id ?: "",
                            name            = name,
                            description     = description,
                            category        = category,
                            imageUrl        = imageUrl,
                            prepTimeMinutes = prepTime.toIntOrNull() ?: 0,
                            cookTimeMinutes = cookTime.toIntOrNull() ?: 0,
                            servings        = servings.toIntOrNull() ?: 1,
                            difficulty      = recipe?.difficulty ?: "medium",
                            ingredients     = ingredients.map { IngredientEntity(it.name, it.amount, it.unit) },
                            steps           = steps.mapIndexed { i, s -> RecipeStepEntity(i + 1, s.description) },
                            tags            = recipe?.tags ?: emptyList(),
                            isFavorite      = recipe?.isFavorite ?: false,
                            externalId      = recipe?.externalId,
                            isImported      = recipe?.isImported ?: false,
                            createdAt       = recipe?.createdAt ?: ""
                        )
                    )
                },
                enabled  = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.save)) }
            Spacer(Modifier.height(32.dp))
        }
    }
}