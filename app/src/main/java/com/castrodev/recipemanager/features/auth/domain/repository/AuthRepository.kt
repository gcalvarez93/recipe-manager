// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/domain/repository/AuthRepository.kt
package com.castrodev.recipemanager.features.auth.domain.repository

import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<UserEntity>
    suspend fun registerWithEmail(email: String, password: String, name: String): Result<UserEntity>
    suspend fun loginWithGoogle(idToken: String): Result<UserEntity>
    suspend fun logout()
    fun getCurrentUser(): UserEntity?
}