// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/domain/entity/UserEntity.kt
package com.castrodev.recipemanager.features.auth.domain.entity

data class UserEntity(
    val id: String,
    val email: String,
    val name: String?,
    val photoUrl: String?
)