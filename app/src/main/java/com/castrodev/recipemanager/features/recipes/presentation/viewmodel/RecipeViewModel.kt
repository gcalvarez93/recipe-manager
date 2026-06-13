// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/presentation/viewmodel/RecipeViewModel.kt
package com.castrodev.recipemanager.features.recipes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.recipemanager.features.recipes.data.repository.RecipeRepositoryImpl
import com.castrodev.recipemanager.features.recipes.domain.entity.ExternalRecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RecipesState {
    object Loading : RecipesState()
    data class Success(val recipes: List<RecipeEntity>) : RecipesState()
    data class Error(val message: String) : RecipesState()
}

sealed class RecipeDetailState {
    object Idle : RecipeDetailState()
    object Loading : RecipeDetailState()
    data class Success(val recipe: RecipeEntity) : RecipeDetailState()
    data class Error(val message: String) : RecipeDetailState()
}

sealed class ExternalSearchState {
    object Idle : ExternalSearchState()
    object Loading : ExternalSearchState()
    data class Success(val recipes: List<ExternalRecipeEntity>) : ExternalSearchState()
    data class Error(val message: String) : ExternalSearchState()
}

class RecipeViewModel : ViewModel() {
    private val repository        = RecipeRepositoryImpl()
    private val getRecipes        = GetRecipesUseCase(repository)
    private val getRecipeById     = GetRecipeByIdUseCase(repository)
    private val createRecipe      = CreateRecipeUseCase(repository)
    private val updateRecipe      = UpdateRecipeUseCase(repository)
    private val deleteRecipe      = DeleteRecipeUseCase(repository)
    private val toggleFavorite    = ToggleFavoriteUseCase(repository)
    private val searchExternal    = SearchExternalRecipesUseCase(repository)
    private val importExternal    = ImportExternalRecipeUseCase(repository)

    private val _recipesState = MutableStateFlow<RecipesState>(RecipesState.Loading)
    val recipesState: StateFlow<RecipesState> = _recipesState

    private val _detailState = MutableStateFlow<RecipeDetailState>(RecipeDetailState.Idle)
    val detailState: StateFlow<RecipeDetailState> = _detailState

    private val _externalState = MutableStateFlow<ExternalSearchState>(ExternalSearchState.Idle)
    val externalState: StateFlow<ExternalSearchState> = _externalState

    private val _actionSuccess = MutableStateFlow(false)
    val actionSuccess: StateFlow<Boolean> = _actionSuccess

    var selectedCategory: String? = null
    var favoritesOnly: Boolean = false

    init { loadRecipes() }

    fun loadRecipes() {
        viewModelScope.launch {
            _recipesState.value = RecipesState.Loading
            getRecipes(selectedCategory, if (favoritesOnly) true else null)
                .onSuccess { _recipesState.value = RecipesState.Success(it) }
                .onFailure { _recipesState.value = RecipesState.Error(it.message ?: "Error") }
        }
    }

    fun loadRecipeById(id: String) {
        viewModelScope.launch {
            _detailState.value = RecipeDetailState.Loading
            getRecipeById(id)
                .onSuccess { _detailState.value = RecipeDetailState.Success(it) }
                .onFailure { _detailState.value = RecipeDetailState.Error(it.message ?: "Error") }
        }
    }

    fun create(recipe: RecipeEntity) {
        viewModelScope.launch {
            createRecipe(recipe)
                .onSuccess { _actionSuccess.value = true; loadRecipes() }
                .onFailure { _recipesState.value = RecipesState.Error(it.message ?: "Error") }
        }
    }

    fun update(recipe: RecipeEntity) {
        viewModelScope.launch {
            updateRecipe(recipe)
                .onSuccess { _actionSuccess.value = true; loadRecipes() }
                .onFailure { _recipesState.value = RecipesState.Error(it.message ?: "Error") }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            deleteRecipe(id)
                .onSuccess { loadRecipes() }
                .onFailure { _recipesState.value = RecipesState.Error(it.message ?: "Error") }
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            toggleFavorite.invoke(id).onSuccess { loadRecipes() }
        }
    }

    fun searchExternal(query: String) {
        viewModelScope.launch {
            _externalState.value = ExternalSearchState.Loading
            searchExternal.invoke(query)
                .onSuccess { _externalState.value = ExternalSearchState.Success(it) }
                .onFailure { _externalState.value = ExternalSearchState.Error(it.message ?: "Error") }
        }
    }

    fun importExternal(recipe: ExternalRecipeEntity) {
        viewModelScope.launch {
            importExternal.invoke(recipe)
                .onSuccess { _actionSuccess.value = true; loadRecipes() }
                .onFailure { _recipesState.value = RecipesState.Error(it.message ?: "Error") }
        }
    }

    fun resetActionSuccess() { _actionSuccess.value = false }
    fun clearExternalSearch() { _externalState.value = ExternalSearchState.Idle }
}