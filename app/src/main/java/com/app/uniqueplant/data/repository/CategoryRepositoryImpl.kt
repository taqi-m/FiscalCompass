package com.app.uniqueplant.data.repository

import com.app.uniqueplant.data.datasource.local.dao.CategoryDao
import com.app.uniqueplant.data.mapper.toCategory
import com.app.uniqueplant.data.mapper.toCategoryEntity
import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override suspend fun insertCategory(category: Category): Long {
        val categoryEntity = category.toCategoryEntity()
        val existingCategory = categoryDao.getCategoryByName(categoryEntity.name)
        if (existingCategory == null) {
            categoryDao.insertCategory(categoryEntity)
            return category.categoryId
        }
        return -1
    }

    override suspend fun updateCategory(category: Category): Int {
        return categoryDao.updateCategory(category.toCategoryEntity())
    }

    override suspend fun deleteCategory(category: Category): Int {
        return categoryDao.deleteCategory(category.toCategoryEntity())
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories().map {
            it.toCategory()
        }
    }

    override suspend fun getIncomeCategoriesWithFlow(): Flow<List<Category>> {
        return categoryDao.getIncomeCategoriesWithFlow().map { categoryEntities ->
            categoryEntities.map { it.toCategory() }
        }
    }

    override suspend fun getExpenseCategoriesWithFlow(): Flow<List<Category>> {
        return categoryDao.getExpenseCategoriesWithFlow().map { categoryEntities ->
            categoryEntities.map { it.toCategory() }
        }
    }

    override suspend fun getIncomeCategories(): List<Category> {
        return categoryDao.getIncomeCategories().map {
            it.toCategory()
        }
    }

    override suspend fun getExpenseCategories(): List<Category> {
        return categoryDao.getExpenseCategories().map {
            it.toCategory()
        }
    }

    override suspend fun getCategoryNameById(id: Long): String? {
        return categoryDao.getCategoryById(id)?.name
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toCategory()
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)?.toCategory()
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