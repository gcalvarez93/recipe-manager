// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/data/repository/AuthRepositoryImpl.kt
package com.castrodev.recipemanager.features.auth.data.repository

import com.castrodev.recipemanager.core.util.safeCall
import com.castrodev.recipemanager.features.auth.data.datasource.AuthRemoteDataSource
import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity
import com.castrodev.recipemanager.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun loginWithEmail(email: String, password: String): Result<UserEntity> =
        safeCall("AuthRepositoryImpl.loginWithEmail") { dataSource.loginWithEmail(email, password).toEntity() }

    override suspend fun registerWithEmail(email: String, password: String, name: String): Result<UserEntity> =
        safeCall("AuthRepositoryImpl.registerWithEmail") { dataSource.registerWithEmail(email, password, name).toEntity() }

    override suspend fun loginWithGoogle(idToken: String): Result<UserEntity> =
        safeCall("AuthRepositoryImpl.loginWithGoogle") { dataSource.loginWithGoogle(idToken).toEntity() }

    override suspend fun logout() = dataSource.logout()

    override fun getCurrentUser(): UserEntity? = dataSource.getCurrentUser()?.toEntity()
}