// Path: app/src/main/java/com/castrodev/recipemanager/features/recipes/presentation/viewmodel/RecipeViewModel.kt
package com.castrodev.recipemanager.features.recipes.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.recipemanager.core.ui.toMessageRes
import com.castrodev.recipemanager.features.recipes.domain.entity.ExternalRecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.entity.RecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecipesState {
    object Loading : RecipesState()
    data class Success(val recipes: List<RecipeEntity>) : RecipesState()
    data class Error(@StringRes val messageRes: Int) : RecipesState()
}

sealed class RecipeDetailState {
    object Idle : RecipeDetailState()
    object Loading : RecipeDetailState()
    data class Success(val recipe: RecipeEntity) : RecipeDetailState()
    data class Error(@StringRes val messageRes: Int) : RecipeDetailState()
}

sealed class ExternalSearchState {
    object Idle : ExternalSearchState()
    object Loading : ExternalSearchState()
    data class Success(val recipes: List<ExternalRecipeEntity>) : ExternalSearchState()
    data class Error(@StringRes val messageRes: Int) : ExternalSearchState()
}

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRecipes: GetRecipesUseCase,
    private val getRecipeById: GetRecipeByIdUseCase,
    private val createRecipe: CreateRecipeUseCase,
    private val updateRecipe: UpdateRecipeUseCase,
    private val deleteRecipe: DeleteRecipeUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val searchExternal: SearchExternalRecipesUseCase,
    private val importExternal: ImportExternalRecipeUseCase
) : ViewModel() {

    private val _recipesState = MutableStateFlow<RecipesState>(RecipesState.Loading)
    val recipesState: StateFlow<RecipesState> = _recipesState

    private val _detailState = MutableStateFlow<RecipeDetailState>(RecipeDetailState.Idle)
    val detailState: StateFlow<RecipeDetailState> = _detailState

    private val _externalState = MutableStateFlow<ExternalSearchState>(ExternalSearchState.Idle)
    val externalState: StateFlow<ExternalSearchState> = _externalState

    private val _actionSuccess = MutableStateFlow(false)
    val actionSuccess: StateFlow<Boolean> = _actionSuccess

    /** `true` mientras se guarda, borra o importa: la UI desactiva el botón para evitar dobles envíos. */
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    /**
     * Error de la última acción de escritura (crear, editar, borrar, favorito, importar).
     * Se publica aparte del estado de la lista: un fallo al guardar no debe borrar lo ya cargado.
     */
    private val _actionError = MutableStateFlow<Int?>(null)
    val actionError: StateFlow<Int?> = _actionError

    var selectedCategory: String? = null
    var favoritesOnly: Boolean = false

    private var loadJob: Job? = null

    init { loadRecipes() }

    fun loadRecipes() {
        // Si hay una carga en curso se descarta: gana siempre la última petición.
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _recipesState.value = RecipesState.Loading
            getRecipes(selectedCategory, if (favoritesOnly) true else null)
                .onSuccess { _recipesState.value = RecipesState.Success(it) }
                .onFailure { _recipesState.value = RecipesState.Error(it.toMessageRes()) }
        }
    }

    fun loadRecipeById(id: String) {
        viewModelScope.launch {
            _detailState.value = RecipeDetailState.Loading
            getRecipeById(id)
                .onSuccess { _detailState.value = RecipeDetailState.Success(it) }
                .onFailure { _detailState.value = RecipeDetailState.Error(it.toMessageRes()) }
        }
    }

    fun create(recipe: RecipeEntity) = perform(markSuccess = true) { createRecipe(recipe) }

    fun update(recipe: RecipeEntity) = perform(markSuccess = true) { updateRecipe(recipe) }

    fun delete(id: String) = perform(markSuccess = false) { deleteRecipe(id) }

    fun importExternal(recipe: ExternalRecipeEntity) =
        perform(markSuccess = true) { importExternal.invoke(recipe) }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            toggleFavorite.invoke(id)
                .onSuccess { loadRecipes() }
                .onFailure { _actionError.value = it.toMessageRes() }
        }
    }

    fun searchExternal(query: String) {
        viewModelScope.launch {
            _externalState.value = ExternalSearchState.Loading
            searchExternal.invoke(query)
                .onSuccess { _externalState.value = ExternalSearchState.Success(it) }
                .onFailure { _externalState.value = ExternalSearchState.Error(it.toMessageRes()) }
        }
    }

    /**
     * Ejecuta una escritura. Si falla, el error se publica en [actionError] y la lista cargada
     * y el formulario abierto se quedan como están. Si va bien, se recarga la lista.
     */
    private fun perform(markSuccess: Boolean, action: suspend () -> Result<Any?>) {
        if (_isSaving.value) return
        _isSaving.value = true
        _actionError.value = null
        viewModelScope.launch {
            try {
                action()
                    .onSuccess {
                        if (markSuccess) _actionSuccess.value = true
                        loadRecipes()
                    }
                    .onFailure { _actionError.value = it.toMessageRes() }
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetActionSuccess() { _actionSuccess.value = false }
    fun clearActionError() { _actionError.value = null }
    fun clearExternalSearch() { _externalState.value = ExternalSearchState.Idle }
}