// Path: app/src/test/java/com/castrodev/recipemanager/fakes/FakeAuthRepository.kt
package com.castrodev.recipemanager.fakes

import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity
import com.castrodev.recipemanager.features.auth.domain.repository.AuthRepository

val testUser = UserEntity(id = "u1", email = "ana@test.com", name = "Ana", photoUrl = null)

/** Repositorio de autenticación en memoria con resultados configurables. */
class FakeAuthRepository : AuthRepository {
    var signedInUser: UserEntity? = null
    var loginResult: Result<UserEntity> = Result.success(testUser)
    var registerResult: Result<UserEntity> = Result.success(testUser)

    var loginCalls = 0
    var registerCalls = 0

    override suspend fun loginWithEmail(email: String, password: String): Result<UserEntity> {
        loginCalls++
        return loginResult
    }

    override suspend fun registerWithEmail(email: String, password: String, name: String): Result<UserEntity> {
        registerCalls++
        return registerResult
    }

    override suspend fun loginWithGoogle(idToken: String): Result<UserEntity> = loginResult

    override suspend fun logout() {
        signedInUser = null
    }

    override fun getCurrentUser(): UserEntity? = signedInUser
}