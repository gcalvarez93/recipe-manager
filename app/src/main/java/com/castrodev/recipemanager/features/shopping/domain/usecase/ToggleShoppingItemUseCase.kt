// Path: app/src/main/java/com/castrodev/recipemanager/features/shopping/domain/usecase/ToggleShoppingItemUseCase.kt
package com.castrodev.recipemanager.features.shopping.domain.usecase
import com.castrodev.recipemanager.features.shopping.domain.repository.ShoppingListRepository
class ToggleShoppingItemUseCase(private val repository: ShoppingListRepository) {
    suspend operator fun invoke(id: String, itemName: String) = repository.toggleItem(id, itemName)
}