// Path: app/src/main/java/com/castrodev/recipemanager/di/BindingsModule.kt
package com.castrodev.recipemanager.di

import com.castrodev.recipemanager.core.network.FirebaseTokenProvider
import com.castrodev.recipemanager.core.network.TokenProvider
import com.castrodev.recipemanager.features.auth.data.repository.AuthRepositoryImpl
import com.castrodev.recipemanager.features.auth.domain.repository.AuthRepository
import com.castrodev.recipemanager.features.mealplan.data.repository.MealPlanRepositoryImpl
import com.castrodev.recipemanager.features.mealplan.domain.repository.MealPlanRepository
import com.castrodev.recipemanager.features.profile.data.repository.ProfileRepositoryImpl
import com.castrodev.recipemanager.features.profile.domain.repository.ProfileRepository
import com.castrodev.recipemanager.features.recipes.data.repository.RecipeRepositoryImpl
import com.castrodev.recipemanager.features.recipes.domain.repository.RecipeRepository
import com.castrodev.recipemanager.features.shopping.data.repository.ShoppingListRepositoryImpl
import com.castrodev.recipemanager.features.shopping.domain.repository.ShoppingListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Enlaza cada interfaz del dominio con su implementación de la capa data. */
@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {

    @Binds @Singleton
    abstract fun bindTokenProvider(impl: FirebaseTokenProvider): TokenProvider

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindRecipeRepository(impl: RecipeRepositoryImpl): RecipeRepository

    @Binds @Singleton
    abstract fun bindMealPlanRepository(impl: MealPlanRepositoryImpl): MealPlanRepository

    @Binds @Singleton
    abstract fun bindShoppingListRepository(impl: ShoppingListRepositoryImpl): ShoppingListRepository

    @Binds @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}