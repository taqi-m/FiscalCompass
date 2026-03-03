package com.fiscal.compass.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fiscal.compass.data.local.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(categoryEntity: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(defaultEntities: List<CategoryEntity>)
    
    @Update
    suspend fun update(categoryEntity: CategoryEntity): Int
    
    @Delete
    suspend fun delete(categoryEntity: CategoryEntity): Int

    @Query("UPDATE categories SET isDeleted = 1, needsSync = 1, updatedAt = :timestamp WHERE categoryId = :categoryId")
    suspend fun markAsDeleted(categoryId: String, timestamp: Long = System.currentTimeMillis()): Int

    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY name ASC")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories WHERE categoryId = :id AND isDeleted = 0")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getCategoryByIdIncludeDeleted(id: String): CategoryEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM expenses WHERE categoryId = :categoryId LIMIT 1)")
    suspend fun isCategoryUsedInExpenses(categoryId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM incomes WHERE categoryId = :categoryId LIMIT 1)")
    suspend fun isCategoryUsedInIncomes(categoryId: String): Boolean

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String) : CategoryEntity?

    @Query("SELECT * FROM categories WHERE isExpenseCategory = 0 AND isDeleted = 0 ORDER BY name ASC")
    fun getIncomeCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isExpenseCategory = 1 AND isDeleted = 0 ORDER BY name ASC")
    fun getExpenseCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isExpenseCategory = 0 AND isDeleted = 0 ORDER BY name ASC")
    fun getIncomeCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE isExpenseCategory = 1 AND isDeleted = 0 ORDER BY name ASC")
    fun getExpenseCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE needsSync = 1")
    suspend fun getUnsyncedCategories(): List<CategoryEntity>

}