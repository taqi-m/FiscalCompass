package com.app.uniqueplant.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.uniqueplant.data.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(categoryEntity: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(defaultEntities: List<CategoryEntity>)
    
    @Update
    suspend fun updateCategory(categoryEntity: CategoryEntity): Int
    
    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity): Int

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>


    
    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?
    
    @Query("SELECT EXISTS(SELECT 1 FROM expenses WHERE categoryId = :categoryId LIMIT 1)")
    suspend fun isCategoryUsedInExpenses(categoryId: Long): Boolean
    
    @Query("SELECT EXISTS(SELECT 1 FROM incomes WHERE categoryId = :categoryId LIMIT 1)")
    suspend fun isCategoryUsedInIncomes(categoryId: Long): Boolean

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String) : CategoryEntity?

    @Query("SELECT * FROM categories WHERE isExpenseCategory = 0 ORDER BY name ASC")
    fun getIncomeCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isExpenseCategory = 1 ORDER BY name ASC")
    fun getExpenseCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isExpenseCategory = 0 ORDER BY name ASC")
    fun getIncomeCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE isExpenseCategory = 1 ORDER BY name ASC")
    fun getExpenseCategories(): List<CategoryEntity>

}