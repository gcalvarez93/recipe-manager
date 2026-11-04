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
    var googleResult: Result<UserEntity> = Result.success(testUser)

    var loginCalls = 0
    var registerCalls = 0
    var googleCalls = 0

    /** Últimos valores recibidos, para comprobar que los casos de uso limpian los datos. */
    var lastEmail: String? = null
    var lastName: String? = null

    override suspend fun loginWithEmail(email: String, password: String): Result<UserEntity> {
        loginCalls++
        lastEmail = email
        return loginResult
    }

    override suspend fun registerWithEmail(email: String, password: String, name: String): Result<UserEntity> {
        registerCalls++
        lastEmail = email
        lastName = name
        return registerResult
    }

    override suspend fun loginWithGoogle(idToken: String): Result<UserEntity> {
        googleCalls++
        return googleResult
    }

    override suspend fun logout() {
        signedInUser = null
    }

    override fun getCurrentUser(): UserEntity? = signedInUser
}