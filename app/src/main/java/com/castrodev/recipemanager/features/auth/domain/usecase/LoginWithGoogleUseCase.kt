// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/domain/usecase/LoginWithGoogleUseCase.kt
package com.castrodev.recipemanager.features.auth.domain.usecase

import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity
import com.castrodev.recipemanager.features.auth.domain.repository.AuthRepository

class LoginWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<UserEntity> =
        repository.loginWithGoogle(idToken)
}