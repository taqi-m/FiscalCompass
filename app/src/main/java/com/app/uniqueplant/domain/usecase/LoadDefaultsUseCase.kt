package com.app.uniqueplant.domain.usecase

import android.util.Log
import com.app.uniqueplant.data.datasource.preferences.SharedPreferencesRepository
import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository

class LoadDefaultsUseCase(
    private val categoryRepository: CategoryRepository,
    private val prefRepository: SharedPreferencesRepository
) {
    suspend fun addDefaultCategories() {
        if (prefRepository.isDefaultCategoriesAdded()) {
            return
        }
        /*val defaultCategories = listOf(
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
        )*/

        val defaultCategories: Map<Category, List<Category>> = mapOf(
            Category(name = "Stone Sales", isExpenseCategory = false) to listOf(
                Category(name = "Crushed Stone", isExpenseCategory = false),
                Category(name = "Blocks & Slabs", isExpenseCategory = false),
                Category(name = "Gravel/Sand", isExpenseCategory = false)
            ),
            Category(name = "Services", isExpenseCategory = false) to listOf(
                Category(name = "Cutting Charges", isExpenseCategory = false),
                Category(name = "Transport Services", isExpenseCategory = false)
            ),
            Category(name = "Logistics", isExpenseCategory = true) to listOf(
                Category(name = "Transport/Fuel", isExpenseCategory = true),
                Category(name = "Vehicle Maintenance", isExpenseCategory = true),
                Category(name = "Driver Wages", isExpenseCategory = true)
            ),
            Category(name = "Labour", isExpenseCategory = true) to listOf(
                Category(name = "Daily Wages", isExpenseCategory = true),
                Category(name = "Skilled Labour", isExpenseCategory = true),
                Category(name = "Overtime", isExpenseCategory = true)
            ),
            Category(name = "Equipment & Maintenance", isExpenseCategory = true) to listOf(
                Category(name = "Machinery Repair", isExpenseCategory = true),
                Category(name = "Fuel for Generators", isExpenseCategory = true),
                Category(name = "Spare Parts", isExpenseCategory = true)
            ),
            Category(name = "Utilities & Bills", isExpenseCategory = true) to listOf(
                Category(name = "Electricity", isExpenseCategory = true),
                Category(name = "Water Supply", isExpenseCategory = true),
                Category(name = "Mobile/Internet", isExpenseCategory = true)
            ),
            Category(name = "Office & Admin", isExpenseCategory = true) to listOf(
                Category(name = "Stationery", isExpenseCategory = true),
                Category(name = "Misc Expenses", isExpenseCategory = true)
            )
        )


        categoryRepository.seedDefaultCategories(defaultCategories)

        Log.d(TAG, "Default categories added successfully")
        prefRepository.setDefaultCategoriesAdded(true)
    }


    companion object {
        private const val TAG = "LoadDefaultsUseCase"
    }
}