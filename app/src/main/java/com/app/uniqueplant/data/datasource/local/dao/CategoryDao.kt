package com.app.uniqueplant.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.uniqueplant.data.model.Category

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    @Update
    suspend fun updateCategory(category: Category)
    
    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategories(): List<Category>
    
    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getCategoryById(id: Long): Category?
    
    @Query("SELECT EXISTS(SELECT 1 FROM expenses WHERE categoryId = :categoryId LIMIT 1)")
    suspend fun isCategoryUsedInExpenses(categoryId: Long): Boolean
    
    @Query("SELECT EXISTS(SELECT 1 FROM incomes WHERE categoryId = :categoryId LIMIT 1)")
    suspend fun isCategoryUsedInIncomes(categoryId: Long): Boolean

    @Query("SELECT name FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String) : String?

    @Query("SELECT * FROM categories WHERE isExpenseCategory = 0 ORDER BY name ASC")
    fun getIncomeCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE isExpenseCategory = 1 ORDER BY name ASC")
    fun getExpenseCategories(): List<Category>
}