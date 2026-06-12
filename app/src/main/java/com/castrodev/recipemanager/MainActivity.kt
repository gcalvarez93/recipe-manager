// Path: app/src/main/java/com/castrodev/recipemanager/MainActivity.kt
package com.castrodev.recipemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.castrodev.recipemanager.core.theme.RecipeManagerTheme
import com.castrodev.recipemanager.navigation.AppNavigation
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            RecipeManagerTheme {
                AppNavigation()
            }
        }
    }
}