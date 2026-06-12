// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/data/model/UserModel.kt
package com.castrodev.recipemanager.features.auth.data.model

import com.castrodev.recipemanager.features.auth.domain.entity.UserEntity

data class UserModel(
    val id: String,
    val email: String,
    val name: String?,
    val photoUrl: String?
) {
    fun toEntity() = UserEntity(id = id, email = email, name = name, photoUrl = photoUrl)
}