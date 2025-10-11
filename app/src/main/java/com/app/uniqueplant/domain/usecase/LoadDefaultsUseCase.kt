package com.app.uniqueplant.domain.usecase

import android.util.Log
import com.app.uniqueplant.domain.model.dataModels.Category
import com.app.uniqueplant.domain.repository.AppPreferenceRepository
import com.app.uniqueplant.domain.repository.CategoryRepository
import javax.inject.Inject

class LoadDefaultsUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val appPreference: AppPreferenceRepository
) {
    suspend fun addDefaultCategories() {
        if (appPreference.isDefaultCategoriesAdded()) {
            return
        }
        Log.d(("ROOM_DEFAULT"), "Seeding default categories")
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
        appPreference.setDefaultCategoriesAdded(true)
    }
}