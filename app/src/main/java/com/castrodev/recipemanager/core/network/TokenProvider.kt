// Path: app/src/main/java/com/castrodev/recipemanager/core/network/TokenProvider.kt
package com.castrodev.recipemanager.core.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

interface TokenProvider {
    suspend fun getToken(): String
}

class FirebaseTokenProvider : TokenProvider {
    override suspend fun getToken(): String {
        val user = FirebaseAuth.getInstance().currentUser
            ?: throw IllegalStateException("No authenticated user")
        return user.getIdToken(false).await().token
            ?: throw IllegalStateException("Failed to get token")
    }
}