package com.app.uniqueplant.data.datasource.local.repository

import com.app.uniqueplant.data.datasource.local.dao.CategoryDao
import com.app.uniqueplant.data.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }
    
    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }
    
    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }
    
    suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)
    }
    
    suspend fun isCategoryUsedInTransactions(categoryId: Long): Boolean {
        return categoryDao.isCategoryUsedInExpenses(categoryId) || 
               categoryDao.isCategoryUsedInIncomes(categoryId)
    }
}