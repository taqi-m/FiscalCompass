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
                name = "Roda",
                color = 0xFFE57373.toInt(),
                isExpenseCategory = false,
                icon = "🍔"
            ),
            Category(
                name = "Bajar",
                color = 0xFF64B5F6.toInt(),
                isExpenseCategory = false,
                icon = "🚗"
            ),
            Category(
                name = "Mix",
                color = 0xFFBA68C8.toInt(),
                isExpenseCategory = false,
                icon = "🎉"
            ),
            Category(
                name = "Scrap",
                color = 0xFF4DB6AC.toInt(),
                isExpenseCategory = false,
                icon = "💊"
            ),
/*            Category(
                name = "Shopping",
                color = 0xFFFFB74D.toInt(),
                isExpenseCategory = true,
                icon = "🛍️"
            ),*/
            Category(
                name = "Dumper",
                color = 0xFF81C784.toInt(),
                isExpenseCategory = true,
                icon = "💼"
            ),
            Category(
                name = "Loader",
                color = 0xFFAED581.toInt(),
                isExpenseCategory = true,
                icon = "🏢"
            ),
            Category(
                name = "Excavator",
                color = 0xFFFFD54F.toInt(),
                isExpenseCategory = true,
                icon = "🎁"
            ),
            Category(
                name = "Plant Maintenance",
                color = 0xFF4FC3F7.toInt(),
                isExpenseCategory = true,
                icon = "📈"
            ),
            Category(
                name = "Diesel",
                color = 0xFF9575CD.toInt(),
                isExpenseCategory = true,
                icon = "💰"
            ),
            Category(
                name = "Mobil Oil",
                color = 0xFFF06292.toInt(),
                isExpenseCategory = true,
                icon = "🛢️"
            ),
            Category(
                name = "Hassan Khata",
                color = 0xFF4DD0E1.toInt(),
                isExpenseCategory = true,
                icon = "🧾"
            ),
            Category(
                name = "Crushing",
                color = 0xFFA1887F.toInt(),
                isExpenseCategory = true,
                icon = "⚖️"
            ),
            Category(
                name = "Stone Purchase",
                color = 0xFF795548.toInt(),
                isExpenseCategory = true,
                icon = "🧱"
            ),
            Category(
                name = "Electricity Bill",
                color = 0xFFFF8A65.toInt(),
                isExpenseCategory = true,
                icon = "💡"
            ),
            Category(
                name = "Salary",
                color = 0xFFDCE775.toInt(),
                isExpenseCategory = true,
                icon = "💵"
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