package com.app.uniqueplant.data.repository

import com.app.uniqueplant.data.datasource.local.dao.CategoryDao
import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository
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

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories()
    }

    override suspend fun getIncomeCategories(): List<Category> {
        return categoryDao.getIncomeCategories()
    }

    override suspend fun getExpenseCategories(): List<Category> {
        return categoryDao.getExpenseCategories()
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