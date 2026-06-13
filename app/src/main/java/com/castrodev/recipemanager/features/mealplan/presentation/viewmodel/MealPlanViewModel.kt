// Path: app/src/main/java/com/castrodev/recipemanager/features/mealplan/presentation/viewmodel/MealPlanViewModel.kt
package com.castrodev.recipemanager.features.mealplan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.recipemanager.features.mealplan.data.repository.MealPlanRepositoryImpl
import com.castrodev.recipemanager.features.mealplan.domain.entity.MealPlanEntity
import com.castrodev.recipemanager.features.mealplan.domain.usecase.DeleteMealPlanEntryUseCase
import com.castrodev.recipemanager.features.mealplan.domain.usecase.GetMealPlanUseCase
import com.castrodev.recipemanager.features.mealplan.domain.usecase.UpsertMealPlanEntryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class MealPlanState {
    object Loading : MealPlanState()
    data class Success(val plan: MealPlanEntity?) : MealPlanState()
    data class Error(val message: String) : MealPlanState()
}

class MealPlanViewModel : ViewModel() {
    private val repository    = MealPlanRepositoryImpl()
    private val getMealPlan   = GetMealPlanUseCase(repository)
    private val upsertEntry   = UpsertMealPlanEntryUseCase(repository)
    private val deleteEntry   = DeleteMealPlanEntryUseCase(repository)

    private val _state = MutableStateFlow<MealPlanState>(MealPlanState.Loading)
    val state: StateFlow<MealPlanState> = _state

    private val calendar = Calendar.getInstance()
    var selectedYear: Int = calendar.get(Calendar.YEAR)
    var selectedWeek: Int = calendar.get(Calendar.WEEK_OF_YEAR)

    init { loadMealPlan() }

    fun loadMealPlan() {
        viewModelScope.launch {
            _state.value = MealPlanState.Loading
            getMealPlan(selectedYear, selectedWeek)
                .onSuccess { _state.value = MealPlanState.Success(it) }
                .onFailure { _state.value = MealPlanState.Error(it.message ?: "Error") }
        }
    }

    fun addEntry(dayOfWeek: String, mealType: String, recipeId: String, recipeName: String) {
        viewModelScope.launch {
            upsertEntry(selectedYear, selectedWeek, dayOfWeek, mealType, recipeId, recipeName)
                .onSuccess { loadMealPlan() }
        }
    }

    fun removeEntry(dayOfWeek: String, mealType: String) {
        viewModelScope.launch {
            deleteEntry(selectedYear, selectedWeek, dayOfWeek, mealType)
                .onSuccess { loadMealPlan() }
        }
    }

    fun previousWeek() {
        selectedWeek--
        if (selectedWeek < 1) { selectedYear--; selectedWeek = 52 }
        loadMealPlan()
    }

    fun nextWeek() {
        selectedWeek++
        if (selectedWeek > 52) { selectedYear++; selectedWeek = 1 }
        loadMealPlan()
    }
}