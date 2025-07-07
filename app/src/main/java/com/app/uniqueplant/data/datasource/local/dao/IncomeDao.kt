package com.app.uniqueplant.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.uniqueplant.data.model.Income
import com.app.uniqueplant.data.model.IncomeWithCategory
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: Income): Long
    
    @Update
    suspend fun updateIncome(income: Income)
    
    @Delete
    suspend fun deleteIncome(income: Income)
    
    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY date DESC")
    fun getAllIncomesByUser(userId: Long): Flow<List<Income>>
    
    @Query("SELECT * FROM incomes WHERE incomeId = :id")
    suspend fun getIncomeById(id: Long): Income?
    
    @Transaction
    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY date DESC")
    fun getIncomesWithCategory(userId: Long): Flow<List<IncomeWithCategory>>
    
    @Query("SELECT * FROM incomes WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getIncomesByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<List<Income>>
    
    @Query("SELECT * FROM incomes WHERE userId = :userId AND categoryId = :categoryId ORDER BY date DESC")
    fun getIncomesByCategory(userId: Long, categoryId: Long): Flow<List<Income>>
    
    @Query("SELECT SUM(amount) FROM incomes WHERE userId = :userId")
    fun getTotalIncomeByUser(userId: Long): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM incomes WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getIncomeSumByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<Double?>
    
    @Query("SELECT categoryId, SUM(amount) as total FROM incomes WHERE userId = :userId GROUP BY categoryId ORDER BY total DESC")
    fun getIncomeSumByCategory(userId: Long): Flow<Map<@MapColumn("categoryId") Long?, @MapColumn("total") Double>>
    
    @Query("SELECT COUNT(*) FROM incomes WHERE userId = :userId")
    suspend fun getIncomeCount(userId: Long): Int
}