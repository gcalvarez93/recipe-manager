// Path: app/src/main/java/com/castrodev/recipemanager/features/profile/domain/entity/ProfileEntity.kt
package com.castrodev.recipemanager.features.profile.domain.entity

data class ProfileEntity(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String,
    val language: String,
    val notifications: Boolean
)