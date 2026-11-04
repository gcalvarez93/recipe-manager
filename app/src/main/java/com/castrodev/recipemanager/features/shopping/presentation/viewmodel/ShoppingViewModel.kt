// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/presentation/viewmodel/ShoppingViewModel.kt
package com.castrodev.recipemanager.features.shopping.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.core.error.AppError
import com.castrodev.recipemanager.core.ui.messageRes
import com.castrodev.recipemanager.core.ui.toMessageRes
import com.castrodev.recipemanager.core.util.toAppError
import com.castrodev.recipemanager.features.shopping.domain.entity.ShoppingListEntity
import com.castrodev.recipemanager.features.shopping.domain.usecase.GenerateShoppingListUseCase
import com.castrodev.recipemanager.features.shopping.domain.usecase.GetShoppingListUseCase
import com.castrodev.recipemanager.features.shopping.domain.usecase.ToggleShoppingItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

sealed class ShoppingState {
    object Loading : ShoppingState()
    object Empty   : ShoppingState()
    data class Success(val list: ShoppingListEntity) : ShoppingState()
    data class Error(@StringRes val messageRes: Int) : ShoppingState()
}

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val getShoppingList: GetShoppingListUseCase,
    private val generateList: GenerateShoppingListUseCase,
    private val toggleItem: ToggleShoppingItemUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ShoppingState>(ShoppingState.Loading)
    val state: StateFlow<ShoppingState> = _state

    /** Error de generar la lista o marcar un artículo. No sustituye a la lista que ya se ve. */
    private val _actionError = MutableStateFlow<Int?>(null)
    val actionError: StateFlow<Int?> = _actionError

    private val calendar = Calendar.getInstance()
    var selectedYear: Int = calendar.get(Calendar.YEAR)
    var selectedWeek: Int = calendar.get(Calendar.WEEK_OF_YEAR)

    private var loadJob: Job? = null

    init { loadShoppingList() }

    fun loadShoppingList() {
        // Al cambiar de semana varias veces seguidas gana siempre la última petición.
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = ShoppingState.Loading
            getShoppingList(selectedYear, selectedWeek)
                .onSuccess { list ->
                    _state.value = if (list != null) ShoppingState.Success(list) else ShoppingState.Empty
                }
                .onFailure { _state.value = ShoppingState.Error(it.toMessageRes()) }
        }
    }

    fun generate() {
        if (_state.value is ShoppingState.Loading) return
        val previous = _state.value
        loadJob?.cancel()
        viewModelScope.launch {
            _state.value = ShoppingState.Loading
            generateList(selectedYear, selectedWeek)
                .onSuccess { loadShoppingList() }
                .onFailure {
                    _state.value = previous
                    val error = it.toAppError()
                    // Solo "no encontrado" significa que esa semana no tiene plan de comidas.
                    _actionError.value =
                        if (error is AppError.NotFound) R.string.error_no_meal_plan else error.messageRes()
                }
        }
    }

    fun toggle(itemName: String) {
        val current = (_state.value as? ShoppingState.Success) ?: return
        viewModelScope.launch {
            toggleItem(current.list.id, itemName)
                .onSuccess { loadShoppingList() }
                .onFailure { _actionError.value = it.toMessageRes() }
        }
    }

    fun clearActionError() { _actionError.value = null }

    fun previousWeek() {
        selectedWeek--
        if (selectedWeek < 1) { selectedYear--; selectedWeek = 52 }
        loadShoppingList()
    }

    fun nextWeek() {
        selectedWeek++
        if (selectedWeek > 52) { selectedYear++; selectedWeek = 1 }
        loadShoppingList()
    }
}