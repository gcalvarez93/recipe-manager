// Path: app/src/main/java/com/castrodev/recipemanager/features/profile/domain/repository/ProfileRepository.kt
package com.castrodev.recipemanager.features.profile.domain.repository

import com.castrodev.recipemanager.features.profile.domain.entity.ProfileEntity

interface ProfileRepository {
    suspend fun getProfile(): Result<ProfileEntity>
    suspend fun updateProfile(name: String, language: String, notifications: Boolean): Result<ProfileEntity>
}