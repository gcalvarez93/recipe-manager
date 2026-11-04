// Path: app/src/test/java/com/castrodev/recipemanager/features/auth/presentation/viewmodel/AuthViewModelTest.kt
package com.castrodev.recipemanager.features.auth.presentation.viewmodel

import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.core.error.AppError
import com.castrodev.recipemanager.core.util.MainDispatcherRule
import com.castrodev.recipemanager.fakes.FakeAuthRepository
import com.castrodev.recipemanager.fakes.testUser
import com.castrodev.recipemanager.features.auth.domain.usecase.GetCurrentUserUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LoginWithEmailUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LoginWithGoogleUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.LogoutUseCase
import com.castrodev.recipemanager.features.auth.domain.usecase.RegisterWithEmailUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

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
    fun `login con credenciales incorrectas publica el texto localizado`() = runTest {
        repository.loginResult = Result.failure(AppError.InvalidCredentials())
        val vm = viewModel()

        vm.signInWithEmail("ana@test.com", "mala")

        assertEquals(AuthState.Error(R.string.error_invalid_credentials), vm.authState.value)
        assertFalse(vm.isAuthenticated.value)
    }

    @Test
    fun `un error tecnico nunca llega a la pantalla`() = runTest {
        repository.loginResult = Result.failure(IllegalStateException("java.lang.NullPointerException en Foo.kt:42"))
        val vm = viewModel()

        vm.signInWithEmail("ana@test.com", "123456")

        assertEquals(AuthState.Error(R.string.error_unknown), vm.authState.value)
    }

    @Test
    fun `sin red muestra el aviso de red`() = runTest {
        repository.loginResult = Result.failure(IOException("Unable to resolve host"))
        val vm = viewModel()

        vm.signInWithEmail("ana@test.com", "123456")

        assertEquals(AuthState.Error(R.string.error_network), vm.authState.value)
    }

    @Test
    fun `campos vacios muestran la validacion sin llamar al repositorio`() = runTest {
        val vm = viewModel()

        vm.signInWithEmail("", "")

        assertEquals(AuthState.Error(R.string.validation_email_password_required), vm.authState.value)
        assertEquals(0, repository.loginCalls)
    }

    @Test
    fun `registro con correo ya usado muestra su mensaje`() = runTest {
        repository.registerResult = Result.failure(AppError.EmailAlreadyInUse())
        val vm = viewModel()

        vm.signUpWithEmail("ana@test.com", "123456", "Ana")

        assertEquals(AuthState.Error(R.string.error_email_in_use), vm.authState.value)
    }

    @Test
    fun `login con Google correcto marca autenticado`() = runTest {
        val vm = viewModel()

        vm.signInWithGoogle("token")

        assertEquals(AuthState.Success(testUser), vm.authState.value)
        assertTrue(vm.isAuthenticated.value)
        assertEquals(1, repository.googleCalls)
    }

    @Test
    fun `login con Google fallido publica el error y no autentica`() = runTest {
        repository.googleResult = Result.failure(AppError.Network())
        val vm = viewModel()

        vm.signInWithGoogle("token")

        assertEquals(AuthState.Error(R.string.error_network), vm.authState.value)
        assertFalse(vm.isAuthenticated.value)
    }

    @Test
    fun `clearError devuelve el estado a Idle`() = runTest {
        repository.loginResult = Result.failure(AppError.InvalidCredentials())
        val vm = viewModel()
        vm.signInWithEmail("ana@test.com", "mala")

        vm.clearError()

        assertEquals(AuthState.Idle, vm.authState.value)
    }

    @Test
    fun `clearError no toca un estado que no sea de error`() = runTest {
        val vm = viewModel()
        vm.signInWithEmail("ana@test.com", "123456")

        vm.clearError()

        assertEquals(AuthState.Success(testUser), vm.authState.value)
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