// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/presentation/viewmodel/ShoppingViewModel.kt
package com.castrodev.recipemanager.features.shopping.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.recipemanager.features.shopping.data.repository.ShoppingListRepositoryImpl
import com.castrodev.recipemanager.features.shopping.domain.entity.ShoppingListEntity
import com.castrodev.recipemanager.features.shopping.domain.usecase.GenerateShoppingListUseCase
import com.castrodev.recipemanager.features.shopping.domain.usecase.GetShoppingListUseCase
import com.castrodev.recipemanager.features.shopping.domain.usecase.ToggleShoppingItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class ShoppingState {
    object Loading : ShoppingState()
    object Empty   : ShoppingState()
    data class Success(val list: ShoppingListEntity) : ShoppingState()
    data class Error(val message: String) : ShoppingState()
}

class ShoppingViewModel : ViewModel() {
    private val repository      = ShoppingListRepositoryImpl()
    private val getShoppingList = GetShoppingListUseCase(repository)
    private val generateList    = GenerateShoppingListUseCase(repository)
    private val toggleItem      = ToggleShoppingItemUseCase(repository)

    private val _state = MutableStateFlow<ShoppingState>(ShoppingState.Loading)
    val state: StateFlow<ShoppingState> = _state

    private val calendar = Calendar.getInstance()
    var selectedYear: Int = calendar.get(Calendar.YEAR)
    var selectedWeek: Int = calendar.get(Calendar.WEEK_OF_YEAR)

    init { loadShoppingList() }

    fun loadShoppingList() {
        viewModelScope.launch {
            _state.value = ShoppingState.Loading
            getShoppingList(selectedYear, selectedWeek)
                .onSuccess { list ->
                    _state.value = if (list != null) ShoppingState.Success(list) else ShoppingState.Empty
                }
                .onFailure { _state.value = ShoppingState.Error(it.message ?: "Error") }
        }
    }

    fun generate() {
        viewModelScope.launch {
            _state.value = ShoppingState.Loading
            generateList(selectedYear, selectedWeek)
                .onSuccess { loadShoppingList() }
                .onFailure {
                    _state.value = ShoppingState.Error("No meal plan for this week. Add meals first.")
                }
        }
    }

    fun toggle(itemName: String) {
        val current = (_state.value as? ShoppingState.Success) ?: return
        viewModelScope.launch {
            toggleItem(current.list.id, itemName)
                .onSuccess { loadShoppingList() }
                .onFailure { _state.value = ShoppingState.Error(it.message ?: "Error") }
        }
    }

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