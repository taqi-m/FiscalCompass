package com.app.uniqueplant.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.uniqueplant.data.model.ExpenseEntity
import com.app.uniqueplant.data.model.ExpenseFullDbo
import com.app.uniqueplant.data.model.ExpenseWithCategoryDbo
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expenseEntity: ExpenseEntity): Long
    
    @Update
    suspend fun updateExpense(expenseEntity: ExpenseEntity)
    
    @Delete
    suspend fun deleteExpense(expenseEntity: ExpenseEntity)

    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpensesByUser(userId: String): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses WHERE expenseId = :id")
    suspend fun getExpenseById(id: Long): ExpenseEntity?
    
    @Transaction
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpensesWithCategory(userId: String): Flow<List<ExpenseWithCategoryDbo>>

    @Transaction
    @Query("SELECT * FROM expenses WHERE expenseId = :id ORDER BY date DESC LIMIT 1")
    suspend fun getSingleFullExpense(id: Long): ExpenseFullDbo

    @Query("""
        SELECT * FROM expenses
        WHERE (:personIds IS NULL OR personId IN (:personIds))
          AND (:categoryIds IS NULL OR categoryId IN (:categoryIds))
          AND (
              (:startDate IS NULL AND :endDate IS NULL)
              OR (:startDate IS NOT NULL AND :endDate IS NULL AND date >= :startDate)
              OR (:startDate IS NULL AND :endDate IS NOT NULL AND date <= :endDate)
              OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND date BETWEEN :startDate AND :endDate)
          )
    """)
    suspend fun getAllFullExpensesFiltered(
        personIds: List<Long>?,   // pass null to ignore
        categoryIds: List<Long>?, // pass null to ignore
        startDate: Long?,         // nullable → open start
        endDate: Long?            // nullable → open end
    ): List<ExpenseEntity>
    
    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRangeForAllUsers(startDate: Date, endDate: Date): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses WHERE userId = :userId AND categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategory(userId: String, categoryId: Long): Flow<List<ExpenseEntity>>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId")
    fun getTotalExpensesByUser(userId: String): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getExpenseSumByDateRange(userId: String, startDate: Date, endDate: Date): Flow<Double?>
    
    @Query("SELECT categoryId, SUM(amount) as total FROM expenses WHERE userId = :userId GROUP BY categoryId ORDER BY total DESC")
    fun getExpenseSumByCategory(userId: String): Flow<Map<@MapColumn("categoryId") Long?, @MapColumn("total") Double>>

    @Query("SELECT COUNT(*) FROM expenses WHERE userId = :userId")
    suspend fun getExpenseCount(userId: String): Int
    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getExpenseSumByMonth(startDate: Date, endDate: Date): Flow<Double>
}
