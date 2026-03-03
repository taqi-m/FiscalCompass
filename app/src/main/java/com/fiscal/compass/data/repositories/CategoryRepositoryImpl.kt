package com.fiscal.compass.data.repositories

import androidx.room.withTransaction
import com.fiscal.compass.data.local.AppDatabase
import com.fiscal.compass.data.local.dao.CategoryDao
import com.fiscal.compass.data.mappers.toCategoryEntity
import com.fiscal.compass.data.mappers.toDomain
import com.fiscal.compass.data.remote.RemoteUtil
import com.fiscal.compass.data.remote.RemoteUtil.ensureValidCategoryId
import com.fiscal.compass.data.remote.RemoteUtil.generateCategoryId
import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.sync.SyncType
import com.fiscal.compass.domain.repository.CategoryRepository
import com.fiscal.compass.domain.sync.AutoSyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val localDb: AppDatabase,
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
        autoSyncManager.triggerSync(SyncType.CATEGORIES)
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
        )

        val dbResult = categoryDao.update(categoryEntity)
        autoSyncManager.triggerSync(SyncType.CATEGORIES)
        return dbResult
    }

    override suspend fun deleteCategory(category: Category): Int {
        val categoryId = category.categoryId
        localDb.withTransaction {
            localDb.categoryDao().markAsDeleted(categoryId)
            localDb.expenseDao().markExpensesAsDeletedByCategory(categoryId)
            localDb.incomeDao().markIncomesAsDeletedByCategory(categoryId)
        }
        autoSyncManager.triggerSync(SyncType.CATEGORIES)
        return 1
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories().map {
            it.toDomain()
        }
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

    override suspend fun getCategoryNameById(id: String): String? {
        return categoryDao.getCategoryById(id)?.name
    }

    override suspend fun getCategoryById(id: String): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)?.toDomain()
    }

    override suspend fun getNextCategoryId(): String {
        val newCategoryId = generateCategoryId()
        val validCategoryId = ensureValidCategoryId(newCategoryId)
        return validCategoryId
    }

    override suspend fun isCategoryUsedInExpenses(categoryId: String): Boolean {
        return categoryDao.isCategoryUsedInExpenses(categoryId)
    }

    override suspend fun isCategoryUsedInIncomes(categoryId: String): Boolean {
        return categoryDao.isCategoryUsedInIncomes(categoryId)
    }
}