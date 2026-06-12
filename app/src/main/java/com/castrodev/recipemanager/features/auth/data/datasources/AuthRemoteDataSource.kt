// Path: app/src/main/java/com/castrodev/recipemanager/features/auth/data/datasource/AuthRemoteDataSource.kt
package com.castrodev.recipemanager.features.auth.data.datasource

import com.castrodev.recipemanager.features.auth.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRemoteDataSource {
    private val auth = FirebaseAuth.getInstance()

    suspend fun loginWithEmail(email: String, password: String): UserModel {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user   = result.user ?: throw IllegalStateException("Login failed")
        return UserModel(id = user.uid, email = user.email ?: "", name = user.displayName, photoUrl = user.photoUrl?.toString())
    }

    suspend fun registerWithEmail(email: String, password: String, name: String): UserModel {
        val result  = auth.createUserWithEmailAndPassword(email, password).await()
        val user    = result.user ?: throw IllegalStateException("Registration failed")
        val request = userProfileChangeRequest { displayName = name }
        user.updateProfile(request).await()
        return UserModel(id = user.uid, email = user.email ?: "", name = name, photoUrl = user.photoUrl?.toString())
    }

    suspend fun loginWithGoogle(idToken: String): UserModel {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result     = auth.signInWithCredential(credential).await()
        val user       = result.user ?: throw IllegalStateException("Google login failed")
        return UserModel(id = user.uid, email = user.email ?: "", name = user.displayName, photoUrl = user.photoUrl?.toString())
    }

    fun logout() = auth.signOut()

    fun getCurrentUser(): UserModel? {
        val user = auth.currentUser ?: return null
        return UserModel(id = user.uid, email = user.email ?: "", name = user.displayName, photoUrl = user.photoUrl?.toString())
    }
}