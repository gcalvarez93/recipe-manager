// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/domain/usecase/RegisterWithEmailUseCase.kt
package com.castrodev.recipemanager.features.auth.domain.usecase

import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity
import com.castrodev.recipemanager.features.auth.domain.repository.AuthRepository

class RegisterWithEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<UserEntity> {
        if (email.isBlank() || password.isBlank() || name.isBlank())
            return Result.failure(IllegalArgumentException("All fields are required"))
        if (password.length < 6)
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        return repository.registerWithEmail(email, password, name)
    }
}