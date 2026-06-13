// Path: app/src/main/java/com/castrodev/recipemanager/features/profile/data/model/ProfileModel.kt
package com.castrodev.recipemanager.features.profile.data.model

import com.castrodev.recipemanager.features.profile.domain.entity.ProfileEntity

data class ProfileModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val language: String = "es",
    val notifications: Boolean = true
) {
    fun toEntity() = ProfileEntity(
        id = id, name = name, email = email,
        photoUrl = photoUrl, language = language, notifications = notifications
    )
}

data class UpdateProfileRequest(
    val name: String,
    val language: String,
    val notifications: Boolean,
    val photoUrl: String = ""
)