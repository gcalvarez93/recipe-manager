// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/domain/usecase/GenerateShoppingListUseCase.kt
package com.castrodev.recipemanager.features.shopping.domain.usecase
import com.castrodev.recipemanager.features.shopping.domain.repository.ShoppingListRepository
import javax.inject.Inject
class GenerateShoppingListUseCase @Inject constructor(private val repository: ShoppingListRepository) {
    suspend operator fun invoke(year: Int, week: Int) = repository.generateShoppingList(year, week)
}