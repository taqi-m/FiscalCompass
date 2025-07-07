package com.app.uniqueplant.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.uniqueplant.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    @Update
    suspend fun updateCategory(category: Category)
    
    @Delete
    suspend fun deleteCategory(category: Category)
    
    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
    fun getAllCategoriesByUser(userId: Long): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getCategoryById(id: Long): Category?
    
    @Query("SELECT * FROM categories WHERE userId = :userId AND isExpenseCategory = 1 ORDER BY name ASC")
    fun getExpenseCategoriesByUser(userId: Long): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE userId = :userId AND isExpenseCategory = 0 ORDER BY name ASC")
    fun getIncomeCategoriesByUser(userId: Long): Flow<List<Category>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM expenses WHERE categoryId = :categoryId LIMIT 1)")
    suspend fun isCategoryUsedInExpenses(categoryId: Long): Boolean
    
    @Query("SELECT EXISTS(SELECT 1 FROM incomes WHERE categoryId = :categoryId LIMIT 1)")
    suspend fun isCategoryUsedInIncomes(categoryId: Long): Boolean
}