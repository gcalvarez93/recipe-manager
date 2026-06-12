// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/presentation/viewmodel/AuthViewModel.kt
package com.castrodev.recipemanager.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.recipemanager.features.auth.data.repository.AuthRepositoryImpl
import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity
import com.castrodev.recipemanager.features.auth.domain.usecase.LoginWithEmailUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LoginWithGoogleUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LogoutUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.RegisterWithEmailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()
    data class Error(val message: String)    : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repository        = AuthRepositoryImpl()
    private val loginWithEmail    = LoginWithEmailUseCase(repository)
    private val registerWithEmail = RegisterWithEmailUseCase(repository)
    private val loginWithGoogle   = LoginWithGoogleUseCase(repository)
    private val logout            = LogoutUseCase(repository)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _isAuthenticated = MutableStateFlow(repository.getCurrentUser() != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            loginWithEmail(email, password)
                .onSuccess { _isAuthenticated.value = true; _authState.value = AuthState.Success(it) }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Unknown error") }
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            registerWithEmail(email, password, name)
                .onSuccess { _isAuthenticated.value = true; _authState.value = AuthState.Success(it) }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Unknown error") }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            loginWithGoogle(idToken)
                .onSuccess { _isAuthenticated.value = true; _authState.value = AuthState.Success(it) }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Unknown error") }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            logout()
            _isAuthenticated.value = false
            _authState.value = AuthState.Idle
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) _authState.value = AuthState.Idle
    }
}