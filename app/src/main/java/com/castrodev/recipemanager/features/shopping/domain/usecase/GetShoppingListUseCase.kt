// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/domain/usecase/GetShoppingListUseCase.kt
package com.castrodev.recipemanager.features.shopping.domain.usecase
import com.castrodev.recipemanager.features.shopping.domain.repository.ShoppingListRepository
class GetShoppingListUseCase(private val repository: ShoppingListRepository) {
    suspend operator fun invoke(year: Int, week: Int) = repository.getShoppingList(year, week)
}