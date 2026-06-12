// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/domain/usecase/LoginWithEmailUseCase.kt
package com.castrodev.recipemanager.features.auth.domain.usecase

import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity
import com.castrodev.recipemanager.features.auth.domain.repository.AuthRepository

class LoginWithEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<UserEntity> {
        if (email.isBlank() || password.isBlank())
            return Result.failure(IllegalArgumentException("Email and password cannot be empty"))
        return repository.loginWithEmail(email, password)
    }
}