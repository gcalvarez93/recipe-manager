// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/domain/usecase/LoginWithEmailUseCase.kt
package com.castrodev.recipemanager.features.auth.domain.usecase

import com.castrodev.recipemanager.core.error.AppError
import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity
import com.castrodev.recipemanager.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<UserEntity> {
        if (email.isBlank() || password.isBlank())
            return Result.failure(AppError.Validation(AppError.Validation.Reason.EmailAndPasswordRequired))
        return repository.loginWithEmail(email.trim(), password)
    }
}