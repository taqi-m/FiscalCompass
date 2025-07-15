package com.app.uniqueplant.domain.usecase

import android.util.Log
import com.app.uniqueplant.data.datasource.preferences.SharedPreferencesRepository
import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository

class LoadDefaultsUseCase(
    private val categoryRepository: CategoryRepository,
    private val prefRepository: SharedPreferencesRepository
) {
    suspend fun addDefaultCategories() {
        if (prefRepository.isDefaultCategoriesAdded()) {
            return
        }
        val defaultCategories = listOf(
            Category(
                name = "Food",
                color = 0xFFE57373.toInt(),
                isExpenseCategory = true,
                icon = "🍔"
            ),
            Category(
                name = "Transport",
                color = 0xFF64B5F6.toInt(),
                isExpenseCategory = true,
                icon = "🚗"
            ),
            Category(
                name = "Entertainment",
                color = 0xFFBA68C8.toInt(),
                isExpenseCategory = true,
                icon = "🎉"
            ),
            Category(
                name = "Health",
                color = 0xFF4DB6AC.toInt(),
                isExpenseCategory = true,
                icon = "💊"
            ),
            Category(
                name = "Shopping",
                color = 0xFFFFB74D.toInt(),
                isExpenseCategory = true,
                icon = "🛍️"
            ),
            Category(
                name = "Salary",
                color = 0xFF81C784.toInt(),
                isExpenseCategory = false,
                icon = "💼"
            ),
            Category(
                name = "Business",
                color = 0xFFAED581.toInt(),
                isExpenseCategory = false,
                icon = "🏢"
            ),
            Category(
                name = "Gift",
                color = 0xFFFFD54F.toInt(),
                isExpenseCategory = false,
                icon = "🎁"
            ),
            Category(
                name = "Investment",
                color = 0xFF4FC3F7.toInt(),
                isExpenseCategory = false,
                icon = "📈"
            ),
            Category(
                name = "Other Income",
                color = 0xFF9575CD.toInt(),
                isExpenseCategory = false,
                icon = "💰"
            )
        )
        for (category in defaultCategories) {
            categoryRepository.insertCategory(category)
        }
        Log.d(TAG, "Default categories added successfully")
        prefRepository.setDefaultCategoriesAdded(true)
    }
    companion object{
        private const val TAG = "LoadDefaultsUseCase"
    }
}