package com.app.uniqueplant.data.repositories

import com.app.uniqueplant.data.managers.AutoSyncManager
import com.app.uniqueplant.data.managers.SyncType
import com.app.uniqueplant.data.mappers.toCategoryEntity
import com.app.uniqueplant.data.mappers.toCategoryTree
import com.app.uniqueplant.data.mappers.toDomain
import com.app.uniqueplant.data.mappers.toEntityList
import com.app.uniqueplant.data.local.dao.CategoryDao
import com.app.uniqueplant.domain.model.base.Category
import com.app.uniqueplant.domain.model.base.CategoryTree
import com.app.uniqueplant.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val autoSyncManager: AutoSyncManager,
) : CategoryRepository {
    override suspend fun insertCategory(category: Category): Long {
        val currentTime = System.currentTimeMillis()
        val categoryEntity = category.toCategoryEntity().copy(
            needsSync = true,
            isSynced = false,
            updatedAt = currentTime,
            createdAt = currentTime,
            lastSyncedAt = null
        )
        val dbResult = categoryDao.insert(categoryEntity)
//        autoSyncManager.triggerSync(SyncType.CATEGORIES)
        return dbResult
    }

    override suspend fun updateCategory(category: Category): Int {
        val currentTime = System.currentTimeMillis()
        val existingCategory = categoryDao.getCategoryById(category.categoryId)

        val categoryEntity = category.toCategoryEntity().copy(
            needsSync = true,
            isSynced = false,
            updatedAt = currentTime,
            createdAt = existingCategory?.createdAt ?: currentTime,
            firestoreId = existingCategory?.firestoreId,
            parentCategoryFirestoreId = existingCategory?.parentCategoryFirestoreId,
            localId = existingCategory?.localId ?: category.toCategoryEntity().localId
        )

        val dbResult = categoryDao.update(categoryEntity)
//        autoSyncManager.triggerSync(SyncType.CATEGORIES)
        return dbResult
    }

    override suspend fun deleteCategory(category: Category): Int {
        categoryDao.markAsDeleted(category.categoryId)
//        autoSyncManager.triggerSync(SyncType.CATEGORIES)
        return 1
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories().map {
            it.toDomain()
        }
    }

    override suspend fun getAllCategoriesTreeFlow(): Flow<CategoryTree> {
        return categoryDao.getAllCategoriesFlow().transform {
            val categories = it.toCategoryTree()
            emit(categories)
        }
    }

    override suspend fun getExpenseCategoriesTreeFLow(): Flow<CategoryTree> {
        return categoryDao.getExpenseCategoriesFlow().map {
            it.toCategoryTree()
        }
    }

    override suspend fun getExpenseCategoriesTree(): CategoryTree {
        return categoryDao.getExpenseCategories().toCategoryTree()
    }

    override suspend fun getIncomeCategoriesTreeFLow(): Flow<CategoryTree> {
        return categoryDao.getIncomeCategoriesFlow().transform {
            val categories = it.toCategoryTree()
            emit(categories)
        }
    }

    override suspend fun getIncomeCategoriesTree(): CategoryTree {
        return categoryDao.getIncomeCategories().toCategoryTree()
    }

    override suspend fun seedDefaultCategories(defaultCategories: Map<Category, List<Category>>) {
        val defaultEntities = defaultCategories.toEntityList()
        categoryDao.insertAll(defaultEntities)
    }

    override suspend fun getIncomeCategoriesWithFlow(): Flow<List<Category>> {
        return categoryDao.getIncomeCategoriesFlow().map { categoryEntities ->
            categoryEntities.map { it.toDomain() }
        }
    }

    override suspend fun getExpenseCategoriesWithFlow(): Flow<List<Category>> {
        return categoryDao.getExpenseCategoriesFlow().map { categoryEntities ->
            categoryEntities.map { it.toDomain() }
        }
    }

    override suspend fun getIncomeCategories(): List<Category> {
        return categoryDao.getIncomeCategories().map {
            it.toDomain()
        }
    }

    override suspend fun getExpenseCategories(): List<Category> {
        return categoryDao.getExpenseCategories().map {
            it.toDomain()
        }
    }

    override suspend fun getCategoryNameById(id: Long): String? {
        return categoryDao.getCategoryById(id)?.name
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)?.toDomain()
    }

    override suspend fun isCategoryUsedInExpenses(categoryId: Long): Boolean {
        return categoryDao.isCategoryUsedInExpenses(categoryId)
    }

    override suspend fun isCategoryUsedInIncomes(categoryId: Long): Boolean {
        return categoryDao.isCategoryUsedInIncomes(categoryId)
    }
}