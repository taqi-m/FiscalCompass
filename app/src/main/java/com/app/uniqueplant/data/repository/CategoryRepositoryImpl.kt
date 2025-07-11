package com.app.uniqueplant.data.repository

import android.util.Log
import com.app.uniqueplant.data.datasource.local.dao.CategoryDao
import com.app.uniqueplant.data.datasource.preferences.SharedPreferencesRepository
import com.app.uniqueplant.data.model.Category
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val prefRepository: SharedPreferencesRepository
) : CategoryRepository {
    override suspend fun addDefaultCategories() {
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
            insertCategory(category)
        }
        Log.d(TAG, "Default categories added successfully")
        prefRepository.setDefaultCategoriesAdded(true)
    }

    override suspend fun insertCategory(category: Category): Long {
        val existingCategory = categoryDao.getCategoryByName(category.name)
        if (existingCategory == null) {
            categoryDao.insertCategory(category)
            return category.categoryId
        }
        return -1
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)
    }

    override suspend fun isCategoryUsedInExpenses(categoryId: Long): Boolean {
        return categoryDao.isCategoryUsedInExpenses(categoryId)
    }

    override suspend fun isCategoryUsedInIncomes(categoryId: Long): Boolean {
        return categoryDao.isCategoryUsedInIncomes(categoryId)
    }

    companion object {
        private const val TAG = "CategoryRepositoryImpl"
    }
}