package com.app.uniqueplant.data.repository

import android.util.Log
import com.app.uniqueplant.data.datasource.local.dao.CategoryDao
import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override suspend fun insertCategory(category: Category): Long {
        val existingCategory = categoryDao.getCategoryByName(category.name)
        if (existingCategory == null) {
            categoryDao.insertCategory(category)
            return category.categoryId
        }
        return -1
    }

    override suspend fun updateCategory(category: Category): Int {
        return categoryDao.updateCategory(category)
    }

    override suspend fun deleteCategory(category: Category): Int {
        return categoryDao.deleteCategory(category)
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories()
    }

    override suspend fun getIncomeCategoriesWithFlow(): Flow<List<Category>> {
        return categoryDao.getIncomeCategoriesWithFlow()
    }

    override suspend fun getExpenseCategoriesWithFlow(): Flow<List<Category>> {
        return categoryDao.getExpenseCategoriesWithFlow()
    }

    override suspend fun getIncomeCategories(): List<Category> {
        return categoryDao.getIncomeCategories()
    }

    override suspend fun getExpenseCategories(): List<Category> {
        return categoryDao.getExpenseCategories()
    }

    override suspend fun getCategoryNameById(id: Long): String? {
        return categoryDao.getCategoryById(id)?.name
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)
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