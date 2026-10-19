// Path: app/src/test/java/com/castrodev/recipemanager/features/recipes/presentation/viewmodel/RecipeViewModelTest.kt
package com.castrodev.recipemanager.features.recipes.presentation.viewmodel

import app.cash.turbine.test
import com.castrodev.recipemanager.fakes.FakeRecipeRepository
import com.castrodev.recipemanager.fakes.testRecipe
import com.castrodev.recipemanager.features.recipes.domain.usecase.CreateRecipeUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.DeleteRecipeUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.GetRecipeByIdUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.GetRecipesUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.ImportExternalRecipeUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.SearchExternalRecipesUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.ToggleFavoriteUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.UpdateRecipeUseCase
import com.castrodev.recipemanager.core.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeRecipeRepository

    @Before
    fun setUp() {
        repository = FakeRecipeRepository()
    }

    private fun viewModel() = RecipeViewModel(
        getRecipes = GetRecipesUseCase(repository),
        getRecipeById = GetRecipeByIdUseCase(repository),
        createRecipe = CreateRecipeUseCase(repository),
        updateRecipe = UpdateRecipeUseCase(repository),
        deleteRecipe = DeleteRecipeUseCase(repository),
        toggleFavorite = ToggleFavoriteUseCase(repository),
        searchExternal = SearchExternalRecipesUseCase(repository),
        importExternal = ImportExternalRecipeUseCase(repository)
    )

    @Test
    fun `al crearse carga las recetas`() = runTest {
        repository.recipesResult = Result.success(listOf(testRecipe()))

        viewModel().recipesState.test {
            assertEquals(RecipesState.Success(listOf(testRecipe())), awaitItem())
        }
    }

    @Test
    fun `si la carga falla publica el error`() = runTest {
        repository.recipesResult = Result.failure(IllegalStateException("sin conexion"))

        viewModel().recipesState.test {
            assertEquals(RecipesState.Error("sin conexion"), awaitItem())
        }
    }

    @Test
    fun `un fallo sin mensaje usa el texto generico`() = runTest {
        repository.recipesResult = Result.failure(RuntimeException())

        viewModel().recipesState.test {
            assertEquals(RecipesState.Error("Error"), awaitItem())
        }
    }

    @Test
    fun `favoritesOnly solo se envia al repositorio cuando esta activo`() = runTest {
        val vm = viewModel()
        assertNull(repository.lastFavoritesOnly)

        vm.favoritesOnly = true
        vm.loadRecipes()

        assertEquals(true, repository.lastFavoritesOnly)
    }

    @Test
    fun `toggleFavorite recarga la lista`() = runTest {
        val vm = viewModel()
        val callsBefore = repository.getRecipesCalls

        vm.toggleFavorite("r1")

        assertEquals("r1", repository.toggledId)
        assertEquals(callsBefore + 1, repository.getRecipesCalls)
    }

    @Test
    fun `crear una receta correctamente activa actionSuccess y recarga`() = runTest {
        val vm = viewModel()
        val callsBefore = repository.getRecipesCalls

        vm.create(testRecipe(id = "nueva"))

        assertEquals("nueva", repository.created?.id)
        assertTrue(vm.actionSuccess.value)
        assertEquals(callsBefore + 1, repository.getRecipesCalls)
    }

    @Test
    fun `resetActionSuccess apaga la marca`() = runTest {
        val vm = viewModel()
        vm.create(testRecipe())

        vm.resetActionSuccess()

        assertEquals(false, vm.actionSuccess.value)
    }
}