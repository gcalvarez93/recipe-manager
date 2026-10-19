// Path: app/src/test/java/com/castrodev/recipemanager/features/auth/presentation/viewmodel/AuthViewModelTest.kt
package com.castrodev.recipemanager.features.auth.presentation.viewmodel

import app.cash.turbine.test
import com.castrodev.recipemanager.fakes.FakeAuthRepository
import com.castrodev.recipemanager.fakes.testUser
import com.castrodev.recipemanager.features.auth.domain.usecase.GetCurrentUserUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LoginWithEmailUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LoginWithGoogleUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LogoutUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.RegisterWithEmailUseCase
import com.castrodev.recipemanager.core.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeAuthRepository

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
    }

    private fun viewModel() = AuthViewModel(
        loginWithEmail = LoginWithEmailUseCase(repository),
        registerWithEmail = RegisterWithEmailUseCase(repository),
        loginWithGoogle = LoginWithGoogleUseCase(repository),
        logout = LogoutUseCase(repository),
        getCurrentUser = GetCurrentUserUseCase(repository)
    )

    @Test
    fun `arranca autenticado si ya hay sesion`() = runTest {
        repository.signedInUser = testUser

        assertTrue(viewModel().isAuthenticated.value)
    }

    @Test
    fun `arranca sin autenticar si no hay sesion`() = runTest {
        assertFalse(viewModel().isAuthenticated.value)
    }

    @Test
    fun `login correcto marca autenticado y publica el usuario`() = runTest {
        val vm = viewModel()

        vm.signInWithEmail("ana@test.com", "123456")

        assertEquals(AuthState.Success(testUser), vm.authState.value)
        assertTrue(vm.isAuthenticated.value)
    }

    @Test
    fun `login fallido publica el mensaje de error`() = runTest {
        repository.loginResult = Result.failure(IllegalStateException("Credenciales incorrectas"))
        val vm = viewModel()

        vm.signInWithEmail("ana@test.com", "mala")

        vm.authState.test {
            assertEquals(AuthState.Error("Credenciales incorrectas"), awaitItem())
        }
        assertFalse(vm.isAuthenticated.value)
    }

    @Test
    fun `clearError devuelve el estado a Idle`() = runTest {
        repository.loginResult = Result.failure(IllegalStateException("x"))
        val vm = viewModel()
        vm.signInWithEmail("ana@test.com", "mala")

        vm.clearError()

        assertEquals(AuthState.Idle, vm.authState.value)
    }

    @Test
    fun `signOut limpia la sesion`() = runTest {
        repository.signedInUser = testUser
        val vm = viewModel()

        vm.signOut()

        assertFalse(vm.isAuthenticated.value)
        assertEquals(AuthState.Idle, vm.authState.value)
    }
}