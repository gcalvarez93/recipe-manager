// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/domain/usecase/GetCurrentUserUseCase.kt
package com.castrodev.recipemanager.features.auth.domain.usecase

import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity
import com.castrodev.recipemanager.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): UserEntity? = repository.getCurrentUser()
}