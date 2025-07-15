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

    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpensesByUser(userId: String): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE expenseId = :id")
    suspend fun getExpenseById(id: Long): Expense?
    
    @Transaction
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpensesWithCategory(userId: String): Flow<List<ExpenseWithCategory>>
    
    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE userId = :userId AND categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategory(userId: String, categoryId: Long): Flow<List<Expense>>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId")
    fun getTotalExpensesByUser(userId: String): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getExpenseSumByDateRange(userId: String, startDate: Date, endDate: Date): Flow<Double?>
    
    @Query("SELECT categoryId, SUM(amount) as total FROM expenses WHERE userId = :userId GROUP BY categoryId ORDER BY total DESC")
    fun getExpenseSumByCategory(userId: String): Flow<Map<@MapColumn("categoryId") Long?, @MapColumn("total") Double>>

    @Query("SELECT COUNT(*) FROM expenses WHERE userId = :userId")
    suspend fun getExpenseCount(userId: String): Int
}
