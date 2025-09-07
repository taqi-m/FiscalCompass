package com.app.uniqueplant.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.uniqueplant.data.model.IncomeEntity
import com.app.uniqueplant.data.model.IncomeFullDbo
import com.app.uniqueplant.data.model.IncomeWithCategoryDbo
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncome(incomeEntity: IncomeEntity): Long
    
    @Update
    suspend fun updateIncome(incomeEntity: IncomeEntity)
    
    @Delete
    suspend fun deleteIncome(incomeEntity: IncomeEntity)

    @Query("SELECT * FROM incomes")
    fun getAllIncomes(): Flow<List<IncomeEntity>>

    @Query("SELECT SUM(amount) FROM incomes WHERE date BETWEEN :startDate AND :endDate")
    fun getIncomeSumByMonth(startDate: Date, endDate: Date): Flow<Double>

    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY date DESC")
    fun getAllIncomesByUser(userId: String): Flow<List<IncomeEntity>>
    
    @Query("SELECT * FROM incomes WHERE incomeId = :id")
    suspend fun getIncomeById(id: Long): IncomeEntity?
    
    @Transaction
    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY date DESC")
    fun getIncomesWithCategory(userId: String): Flow<List<IncomeWithCategoryDbo>>

    @Transaction
    @Query("SELECT * FROM incomes WHERE incomeId = :id ORDER BY date DESC LIMIT 1")
    suspend fun getSingleFullIncome(id: Long): IncomeFullDbo

    @Query("""
        SELECT * FROM incomes
        WHERE (:personIds IS NULL OR personId IN (:personIds))
          AND (:categoryIds IS NULL OR categoryId IN (:categoryIds))
          AND (
              (:startDate IS NULL AND :endDate IS NULL)
              OR (:startDate IS NOT NULL AND :endDate IS NULL AND date >= :startDate)
              OR (:startDate IS NULL AND :endDate IS NOT NULL AND date <= :endDate)
              OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND date BETWEEN :startDate AND :endDate)
          )
    """)
    suspend fun getAllFullIncomesFiltered(
        personIds: List<Long>?,   // pass null to ignore
        categoryIds: List<Long>?, // pass null to ignore
        startDate: Long?,         // nullable → open start
        endDate: Long?            // nullable → open end
    ): List<IncomeEntity>
    
    @Query("SELECT * FROM incomes WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getIncomesByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getIncomesByDateRangeForAllUsers(startDate: Date, endDate: Date): Flow<List<IncomeEntity>>
    
    @Query("SELECT * FROM incomes WHERE userId = :userId AND categoryId = :categoryId ORDER BY date DESC")
    fun getIncomesByCategory(userId: String, categoryId: Long): Flow<List<IncomeEntity>>
    
    @Query("SELECT SUM(amount) FROM incomes WHERE userId = :userId")
    fun getTotalIncomeByUser(userId: String): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM incomes WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getIncomeSumByDateRange(userId: String, startDate: Date, endDate: Date): Flow<Double?>
    
    @Query("SELECT categoryId, SUM(amount) as total FROM incomes WHERE userId = :userId GROUP BY categoryId ORDER BY total DESC")
    fun getIncomeSumByCategory(userId: String): Flow<Map<@MapColumn("categoryId") Long?, @MapColumn("total") Double>>
    
    @Query("SELECT COUNT(*) FROM incomes WHERE userId = :userId")
    suspend fun getIncomeCount(userId: String): Int
}