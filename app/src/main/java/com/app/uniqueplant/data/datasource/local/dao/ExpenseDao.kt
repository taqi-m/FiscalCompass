package com.app.uniqueplant.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.MapColumn
import com.app.uniqueplant.data.model.Expense
import com.app.uniqueplant.data.model.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long
    
    @Update
    suspend fun updateExpense(expense: Expense)
    
    @Delete
    suspend fun deleteExpense(expense: Expense)
    
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpensesByUser(userId: Long): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE expenseId = :id")
    suspend fun getExpenseById(id: Long): Expense?
    
    @Transaction
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpensesWithCategory(userId: Long): Flow<List<ExpenseWithCategory>>
    
    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE userId = :userId AND categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategory(userId: Long, categoryId: Long): Flow<List<Expense>>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId")
    fun getTotalExpensesByUser(userId: Long): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getExpenseSumByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<Double?>
    
    @Query("SELECT categoryId, SUM(amount) as total FROM expenses WHERE userId = :userId GROUP BY categoryId ORDER BY total DESC")
    fun getExpenseSumByCategory(userId: Long): Flow<Map<@MapColumn("categoryId") Long?, @MapColumn("total") Double>>

    @Query("SELECT COUNT(*) FROM expenses WHERE userId = :userId")
    suspend fun getExpenseCount(userId: Long): Int
}
