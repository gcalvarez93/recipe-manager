// Path: app/src/test/java/com/castrodev/recipemanager/features/recipes/presentation/viewmodel/RecipeViewModelTest.kt
package com.castrodev.recipemanager.features.recipes.presentation.viewmodel

import com.castrodev.recipemanager.R
import com.castrodev.recipemanager.core.util.MainDispatcherRule
import com.castrodev.recipemanager.fakes.FakeRecipeRepository
import com.castrodev.recipemanager.fakes.testRecipe
import com.castrodev.recipemanager.features.recipes.domain.entity.ExternalRecipeEntity
import com.castrodev.recipemanager.features.recipes.domain.usecase.CreateRecipeUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.DeleteRecipeUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.GetRecipeByIdUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.GetRecipesUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.ImportExternalRecipeUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.SearchExternalRecipesUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.ToggleFavoriteUseCase
import com.castrodev.recipemanager.features.recipes.domain.usecase.UpdateRecipeUseCase
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

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

    // --- Carga -----------------------------------------------------------------------------

    @Test
    fun `al crearse carga las recetas`() = runTest {
        repository.recipesResult = Result.success(listOf(testRecipe()))

        assertEquals(RecipesState.Success(listOf(testRecipe())), viewModel().recipesState.value)
    }

    @Test
    fun `si la carga falla por la red publica el aviso de red`() = runTest {
        repository.recipesResult = Result.failure(IOException("sin conexion"))

        assertEquals(RecipesState.Error(R.string.error_network), viewModel().recipesState.value)
    }

    @Test
    fun `el mensaje tecnico de una excepcion nunca se muestra`() = runTest {
        repository.recipesResult = Result.failure(IllegalStateException("Cannot invoke method on null at Foo.kt:42"))

        assertEquals(RecipesState.Error(R.string.error_unknown), viewModel().recipesState.value)
    }

    @Test
    fun `un fallo sin mensaje usa el texto generico`() = runTest {
        repository.recipesResult = Result.failure(RuntimeException())

        assertEquals(RecipesState.Error(R.string.error_unknown), viewModel().recipesState.value)
    }

    @Test
    fun `favoritesOnly solo se envia al repositorio cuando esta activo`() = runTest {
        val vm = viewModel()
        assertNull(repository.lastFavoritesOnly)

        vm.favoritesOnly = true
        vm.loadRecipes()

        assertEquals(true, repository.lastFavoritesOnly)
    }

    // --- Favoritos -------------------------------------------------------------------------

    @Test
    fun `toggleFavorite recarga la lista`() = runTest {
        val vm = viewModel()
        val callsBefore = repository.getRecipesCalls

        vm.toggleFavorite("r1")

        assertEquals("r1", repository.toggledId)
        assertEquals(callsBefore + 1, repository.getRecipesCalls)
    }

    @Test
    fun `si toggleFavorite falla avisa y no toca la lista`() = runTest {
        repository.recipesResult = Result.success(listOf(testRecipe()))
        repository.toggleResult = Result.failure(IOException())
        val vm = viewModel()
        val callsBefore = repository.getRecipesCalls

        vm.toggleFavorite("r1")

        assertEquals(R.string.error_network, vm.actionError.value)
        assertEquals(RecipesState.Success(listOf(testRecipe())), vm.recipesState.value)
        assertEquals(callsBefore, repository.getRecipesCalls)
    }

    // --- Crear -----------------------------------------------------------------------------

    @Test
    fun `crear una receta correctamente activa actionSuccess y recarga`() = runTest {
        val vm = viewModel()
        val callsBefore = repository.getRecipesCalls

        vm.create(testRecipe(id = "nueva"))

        assertEquals("nueva", repository.created?.id)
        assertTrue(vm.actionSuccess.value)
        assertNull(vm.actionError.value)
        assertFalse(vm.isSaving.value)
        assertEquals(callsBefore + 1, repository.getRecipesCalls)
    }

    @Test
    fun `si crear falla no cierra el formulario ni reemplaza la lista`() = runTest {
        repository.recipesResult = Result.success(listOf(testRecipe()))
        repository.createResult = Result.failure(IOException())
        val vm = viewModel()
        val callsBefore = repository.getRecipesCalls

        vm.create(testRecipe(id = "nueva"))

        assertFalse(vm.actionSuccess.value)
        assertEquals(R.string.error_network, vm.actionError.value)
        assertEquals(RecipesState.Success(listOf(testRecipe())), vm.recipesState.value)
        assertEquals(callsBefore, repository.getRecipesCalls)
        assertFalse(vm.isSaving.value)
    }

    @Test
    fun `crear una receta sin nombre muestra la validacion y no llama al repositorio`() = runTest {
        val vm = viewModel()

        vm.create(testRecipe(name = "   "))

        assertEquals(R.string.validation_recipe_name_required, vm.actionError.value)
        assertNull(repository.created)
        assertFalse(vm.actionSuccess.value)
    }

    @Test
    fun `crear envia el nombre sin espacios sobrantes`() = runTest {
        val vm = viewModel()

        vm.create(testRecipe(name = "  Paella  "))

        assertEquals("Paella", repository.created?.name)
    }

    // --- Editar y borrar -------------------------------------------------------------------

    @Test
    fun `editar correctamente activa actionSuccess`() = runTest {
        val vm = viewModel()

        vm.update(testRecipe())

        assertEquals(1, repository.updateCalls)
        assertTrue(vm.actionSuccess.value)
    }

    @Test
    fun `si editar falla se queda el error y no hay exito`() = runTest {
        repository.updateResult = Result.failure(IOException())
        val vm = viewModel()

        vm.update(testRecipe())

        assertFalse(vm.actionSuccess.value)
        assertEquals(R.string.error_network, vm.actionError.value)
    }

    @Test
    fun `editar con nombre vacio no llama al repositorio`() = runTest {
        val vm = viewModel()

        vm.update(testRecipe(name = ""))

        assertEquals(0, repository.updateCalls)
        assertEquals(R.string.validation_recipe_name_required, vm.actionError.value)
    }

    @Test
    fun `borrar correctamente recarga la lista`() = runTest {
        val vm = viewModel()
        val callsBefore = repository.getRecipesCalls

        vm.delete("r1")

        assertEquals("r1", repository.deletedId)
        assertEquals(callsBefore + 1, repository.getRecipesCalls)
    }

    @Test
    fun `si borrar falla avisa y la receta sigue en la lista`() = runTest {
        repository.recipesResult = Result.success(listOf(testRecipe()))
        repository.deleteResult = Result.failure(IOException())
        val vm = viewModel()

        vm.delete("r1")

        assertEquals(R.string.error_network, vm.actionError.value)
        assertEquals(RecipesState.Success(listOf(testRecipe())), vm.recipesState.value)
    }

    // --- Busqueda externa e importacion ----------------------------------------------------

    @Test
    fun `una busqueda fallida publica el error en su propio estado`() = runTest {
        repository.searchResult = Result.failure(IOException())
        val vm = viewModel()

        vm.searchExternal("pasta")

        assertEquals(ExternalSearchState.Error(R.string.error_network), vm.externalState.value)
    }

    @Test
    fun `importar correctamente activa actionSuccess`() = runTest {
        val vm = viewModel()

        vm.importExternal(mockk<ExternalRecipeEntity>(relaxed = true))

        assertTrue(vm.actionSuccess.value)
    }

    @Test
    fun `si importar falla se queda el error para mostrarlo en el dialogo`() = runTest {
        repository.importResult = Result.failure(IOException())
        val vm = viewModel()

        vm.importExternal(mockk<ExternalRecipeEntity>(relaxed = true))

        assertFalse(vm.actionSuccess.value)
        assertEquals(R.string.error_network, vm.actionError.value)
    }

    // --- Marcas de estado ------------------------------------------------------------------

    @Test
    fun `un nuevo intento limpia el error anterior`() = runTest {
        repository.createResult = Result.failure(IOException())
        val vm = viewModel()
        vm.create(testRecipe())
        assertEquals(R.string.error_network, vm.actionError.value)

        repository.createResult = Result.success("id")
        vm.create(testRecipe())

        assertNull(vm.actionError.value)
        assertTrue(vm.actionSuccess.value)
    }

    @Test
    fun `clearActionError limpia el error`() = runTest {
        repository.createResult = Result.failure(IOException())
        val vm = viewModel()
        vm.create(testRecipe())

        vm.clearActionError()

        assertNull(vm.actionError.value)
    }

    @Test
    fun `resetActionSuccess apaga la marca`() = runTest {
        val vm = viewModel()
        vm.create(testRecipe())

        vm.resetActionSuccess()

        assertEquals(false, vm.actionSuccess.value)
    }
}