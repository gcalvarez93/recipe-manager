// Path: app/src/test/java/com/castrodev/recipemanager/features/auth/domain/usecase/AuthUseCasesTest.kt
package com.castrodev.recipemanager.features.auth.domain.usecase

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

    @Test
    fun `login con campos vacios falla sin llamar al repositorio`() = runTest {
        val result = LoginWithEmailUseCase(repository)("  ", "")

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(0, repository.loginCalls)
    }

    @Test
    fun `login valido delega en el repositorio`() = runTest {
        val result = LoginWithEmailUseCase(repository)("ana@test.com", "123456")

        assertEquals(testUser, result.getOrNull())
        assertEquals(1, repository.loginCalls)
    }

    @Test
    fun `registro rechaza contrasenas de menos de 6 caracteres`() = runTest {
        val result = RegisterWithEmailUseCase(repository)("ana@test.com", "123", "Ana")

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(0, repository.registerCalls)
    }

    @Test
    fun `registro rechaza campos vacios`() = runTest {
        val result = RegisterWithEmailUseCase(repository)("ana@test.com", "123456", "")

        assertTrue(result.isFailure)
        assertEquals(0, repository.registerCalls)
    }

    @Test
    fun `registro valido delega en el repositorio`() = runTest {
        val result = RegisterWithEmailUseCase(repository)("ana@test.com", "123456", "Ana")

        assertEquals(testUser, result.getOrNull())
        assertEquals(1, repository.registerCalls)
    }
}