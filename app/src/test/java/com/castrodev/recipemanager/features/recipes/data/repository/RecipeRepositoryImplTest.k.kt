// Path: app/src/test/java/com/castrodev/recipemanager/features/recipes/data/repository/RecipeRepositoryImplTest.kt
package com.castrodev.recipemanager.features.recipes.data.repository

import com.castrodev.recipemanager.features.recipes.data.datasource.RecipeRemoteDataSource
import com.castrodev.recipemanager.features.recipes.data.model.RecipeModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.IOException

class RecipeRepositoryImplTest {

    private lateinit var dataSource: RecipeRemoteDataSource
    private lateinit var repository: RecipeRepositoryImpl

    @Before
    fun setUp() {
        dataSource = mockk()
        repository = RecipeRepositoryImpl(dataSource)
    }

    @Test
    fun `getRecipes convierte los modelos de la API en entidades`() = runTest {
        coEvery { dataSource.getRecipes(null, null) } returns
                listOf(RecipeModel(id = "r1", name = "Tortilla", isFavorite = true))

        val result = repository.getRecipes(null, null)

        val recipe = result.getOrThrow().single()
        assertEquals("r1", recipe.id)
        assertEquals("Tortilla", recipe.name)
        assertTrue(recipe.isFavorite)
    }

    @Test
    fun `getRecipes devuelve fallo cuando falla la red`() = runTest {
        val error = IOException("sin red")
        coEvery { dataSource.getRecipes(any(), any()) } throws error

        val result = repository.getRecipes("Cena", null)

        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `toggleFavorite devuelve fallo si el datasource lanza`() = runTest {
        coEvery { dataSource.toggleFavorite("r1") } throws IOException("HTTP 500")

        assertTrue(repository.toggleFavorite("r1").isFailure)
    }

    @Test
    fun `una cancelacion de la corrutina no se convierte en fallo`() = runTest {
        coEvery { dataSource.getRecipeById("r1") } throws CancellationException("cancelada")

        try {
            repository.getRecipeById("r1")
            fail("Se esperaba que la cancelacion se propagara")
        } catch (e: CancellationException) {
            assertEquals("cancelada", e.message)
        }
    }
}