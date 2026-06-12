// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/domain/usecase/LogoutUseCase.kt
package com.castrodev.recipemanager.features.auth.domain.usecase

import com.castrodev.recipemanager.features.auth.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}