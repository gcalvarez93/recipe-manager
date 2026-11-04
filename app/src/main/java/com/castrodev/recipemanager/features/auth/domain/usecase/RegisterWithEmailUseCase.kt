// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/domain/usecase/RegisterWithEmailUseCase.kt
package com.castrodev.recipemanager.features.auth.domain.usecase

import com.castrodev.recipemanager.core.error.AppError
import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity
import com.castrodev.recipemanager.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<UserEntity> {
        if (email.isBlank() || password.isBlank() || name.isBlank())
            return Result.failure(AppError.Validation(AppError.Validation.Reason.AllFieldsRequired))
        if (password.length < MIN_PASSWORD_LENGTH)
            return Result.failure(AppError.Validation(AppError.Validation.Reason.PasswordTooShort))
        return repository.registerWithEmail(email.trim(), password, name.trim())
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}