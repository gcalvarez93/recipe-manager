// Path: app/src/test/java/com/castrodev/recipemanager/features/auth/domain/usecase/AuthUseCasesTest.kt
package com.castrodev.recipemanager.features.auth.domain.usecase

import com.castrodev.recipemanager.core.error.AppError
import com.castrodev.recipemanager.core.error.AppError.Validation.Reason
import com.castrodev.recipemanager.fakes.FakeAuthRepository
import com.castrodev.recipemanager.fakes.testUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthUseCasesTest {

    private lateinit var repository: FakeAuthRepository

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
    }

    private fun Result<*>.validationReason(): Reason? =
        (exceptionOrNull() as? AppError.Validation)?.reason

    @Test
    fun `login con campos vacios falla sin llamar al repositorio`() = runTest {
        val result = LoginWithEmailUseCase(repository)("  ", "")

        assertEquals(Reason.EmailAndPasswordRequired, result.validationReason())
        assertEquals(0, repository.loginCalls)
    }

    @Test
    fun `login valido delega en el repositorio`() = runTest {
        val result = LoginWithEmailUseCase(repository)("ana@test.com", "123456")

        assertEquals(testUser, result.getOrNull())
        assertEquals(1, repository.loginCalls)
    }

    @Test
    fun `login envia el correo sin espacios sobrantes`() = runTest {
        LoginWithEmailUseCase(repository)("  ana@test.com ", "123456")

        assertEquals("ana@test.com", repository.lastEmail)
    }

    @Test
    fun `registro rechaza contrasenas de menos de 6 caracteres`() = runTest {
        val result = RegisterWithEmailUseCase(repository)("ana@test.com", "123", "Ana")

        assertEquals(Reason.PasswordTooShort, result.validationReason())
        assertEquals(0, repository.registerCalls)
    }

    @Test
    fun `registro rechaza campos vacios`() = runTest {
        val result = RegisterWithEmailUseCase(repository)("ana@test.com", "123456", "")

        assertEquals(Reason.AllFieldsRequired, result.validationReason())
        assertEquals(0, repository.registerCalls)
    }

    @Test
    fun `registro valido delega en el repositorio`() = runTest {
        val result = RegisterWithEmailUseCase(repository)("ana@test.com", "123456", "Ana")

        assertEquals(testUser, result.getOrNull())
        assertEquals(1, repository.registerCalls)
    }

    @Test
    fun `registro envia correo y nombre sin espacios sobrantes`() = runTest {
        RegisterWithEmailUseCase(repository)(" ana@test.com ", "123456", "  Ana ")

        assertEquals("ana@test.com", repository.lastEmail)
        assertEquals("Ana", repository.lastName)
    }

    @Test
    fun `el error del repositorio llega tal cual al llamador`() = runTest {
        val error = AppError.EmailAlreadyInUse()
        repository.registerResult = Result.failure(error)

        val result = RegisterWithEmailUseCase(repository)("ana@test.com", "123456", "Ana")

        assertTrue(result.exceptionOrNull() === error)
    }
}