// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/presentation/viewmodel/AuthViewModel.kt
package com.castrodev.recipemanager.features.auth.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.core.network.SessionEvents
import com.castrodev.recipemanager.core.ui.toMessageRes
import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity
import com.castrodev.recipemanager.features.auth.domain.usecase.GetCurrentUserUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LoginWithEmailUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LoginWithGoogleUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LogoutUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.RegisterWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()

    /** [messageRes] es un texto localizado y apto para el usuario, nunca el mensaje de la excepción. */
    data class Error(@StringRes val messageRes: Int) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginWithEmail: LoginWithEmailUseCase,
    private val registerWithEmail: RegisterWithEmailUseCase,
    private val loginWithGoogle: LoginWithGoogleUseCase,
    private val logout: LogoutUseCase,
    getCurrentUser: GetCurrentUserUseCase,
    sessionEvents: SessionEvents
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _isAuthenticated = MutableStateFlow(getCurrentUser() != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    /** Avisos de que el servidor ya no acepta la sesión. Los recoge la navegación. */
    val sessionExpired: Flow<Unit> = sessionEvents.expired

    fun signInWithEmail(email: String, password: String) =
        authenticate { loginWithEmail(email, password) }

    fun signUpWithEmail(email: String, password: String, name: String) =
        authenticate { registerWithEmail(email, password, name) }

    fun signInWithGoogle(idToken: String) =
        authenticate { loginWithGoogle(idToken) }

    /** Un solo intento a la vez: un segundo toque mientras hay uno en curso se ignora. */
    private fun authenticate(request: suspend () -> Result<UserEntity>) {
        if (_authState.value is AuthState.Loading) return
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            request()
                .onSuccess { _isAuthenticated.value = true; _authState.value = AuthState.Success(it) }
                .onFailure { _authState.value = AuthState.Error(it.toMessageRes()) }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            logout()
            _isAuthenticated.value = false
            _authState.value = AuthState.Idle
        }
    }

    /**
     * Cierra la sesión cuando el servidor ya no la acepta y deja el motivo para que el login lo
     * muestre. Devuelve `true` si había una sesión que cerrar: varios avisos seguidos solo
     * provocan una vuelta al login.
     */
    suspend fun onSessionExpired(): Boolean {
        if (!_isAuthenticated.value) return false
        logout()
        _isAuthenticated.value = false
        _authState.value = AuthState.Error(R.string.error_unauthorized)
        return true
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) _authState.value = AuthState.Idle
    }
}