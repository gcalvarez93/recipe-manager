// Path: app/src/main/java/com/castrodev/recipemanager/features/profile/data/repository/ProfileRepositoryImpl.kt
package com.castrodev.recipemanager.features.profile.data.repository

import com.castrodev.recipemanager.core.util.safeCall
import com.castrodev.recipemanager.features.profile.data.datasource.ProfileRemoteDataSource
import com.castrodev.recipemanager.features.profile.domain.entity.ProfileEntity
import com.castrodev.recipemanager.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val dataSource: ProfileRemoteDataSource
) : ProfileRepository {
    override suspend fun getProfile(): Result<ProfileEntity> =
        safeCall("ProfileRepositoryImpl.getProfile") { dataSource.getProfile().toEntity() }

    override suspend fun updateProfile(name: String, language: String, notifications: Boolean): Result<ProfileEntity> =
        safeCall("ProfileRepositoryImpl.updateProfile") { dataSource.updateProfile(name, language, notifications).toEntity() }
}