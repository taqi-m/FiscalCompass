package com.fiscal.compass.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fiscal.compass.data.local.model.IncomeEntity
import com.fiscal.compass.data.local.model.IncomeFullDbo
import com.fiscal.compass.data.local.model.IncomeWithCategoryDbo
import com.fiscal.compass.data.local.model.IncomeWithPersonDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incomeEntity: IncomeEntity): Long

    @Update
    suspend fun update(incomeEntity: IncomeEntity)

    @Delete
    suspend fun delete(incomeEntity: IncomeEntity)

    @Query("SELECT * FROM incomes WHERE isDeleted = 0")
    fun getAll(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE userId = :userId AND isDeleted = 0 ORDER BY date DESC")
    fun getAllByUser(userId: String): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE categoryId = :categoryId AND isDeleted = 0 ORDER BY date DESC")
    fun getAllByCategory(categoryId: String): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE personId = :personId AND isDeleted = 0 ORDER BY date DESC")
    fun getAllByPerson(personId: Long): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE isDeleted = 0 AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getAllByDateRange(startDate: Long, endDate: Long): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE incomeId = :id AND isDeleted = 0")
    suspend fun getById(id: String): IncomeEntity?

    @Query("SELECT SUM(amount) FROM incomes WHERE userId = :userId AND isDeleted = 0")
    fun getSumByUser(userId: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM incomes WHERE (:userId IS NULL OR userId = :userId) AND isDeleted = 0 AND date BETWEEN :startDate AND :endDate")
    fun getSumByDateRange(userId:String?, startDate: Long, endDate: Long): Flow<Double>

    @Query("SELECT SUM(amount) FROM incomes WHERE personId = :personId AND isDeleted = 0")
    fun getSumByPerson(personId: Long): Flow<Double>

    @Transaction
    @Query("SELECT * FROM incomes WHERE userId = :userId AND isDeleted = 0 ORDER BY date DESC")
    fun getIncomesWithCategory(userId: String): Flow<List<IncomeWithCategoryDbo>>

    @Transaction
    @Query("SELECT * FROM incomes WHERE personId = :personId AND isDeleted = 0 ORDER BY date DESC")
    fun getIncomesWithPerson(personId: String): Flow<List<IncomeWithPersonDbo>>

    @Transaction
    @Query("SELECT * FROM incomes WHERE incomeId = :id AND isDeleted = 0 ORDER BY date DESC LIMIT 1")
    suspend fun getSingleFullIncome(id: String): IncomeFullDbo?

    @Query(
        """
        SELECT * FROM incomes
        WHERE isDeleted = 0
          AND (CASE WHEN :hasUserIds THEN userId IN (:userIds) ELSE 1 END)
          AND (CASE WHEN :hasPersonIds THEN personId IN (:personIds) ELSE 1 END)
          AND (CASE WHEN :hasCategoryIds THEN categoryId IN (:categoryIds) ELSE 1 END)
          AND (
              (:startDate IS NULL AND :endDate IS NULL)
              OR (:startDate IS NOT NULL AND :endDate IS NULL AND date >= :startDate)
              OR (:startDate IS NULL AND :endDate IS NOT NULL AND date <= :endDate)
              OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND date BETWEEN :startDate AND :endDate)
          )
    """
    )
    fun getAllFiltered(
        userIds: List<String>,
        hasUserIds: Boolean,
        personIds: List<String>,
        hasPersonIds: Boolean,
        categoryIds: List<String>,
        hasCategoryIds: Boolean,
        startDate: Long? = null,         // nullable → open start
        endDate: Long?  = null            // nullable → open end
    ): Flow<List<IncomeEntity>>

    @Query("SELECT COUNT(*) FROM incomes WHERE userId = :userId AND isDeleted = 0")
    suspend fun getIncomeCount(userId: String): Int

    /**
     * * Sync-related queries
     */

    @Query("UPDATE incomes SET isDeleted = 1, needsSync = 1, updatedAt = :timestamp WHERE incomeId = :incomeId")
    suspend fun markIncomeAsDeleted(incomeId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE incomes SET isDeleted = 1, needsSync = 1, updatedAt = :timestamp WHERE categoryId = :categoryId AND isDeleted = 0")
    suspend fun markIncomesAsDeletedByCategory(categoryId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM incomes WHERE needsSync = 1")
    fun getUnsyncedIncomeCount(): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM incomes WHERE needsSync = 1)")
    suspend fun hasUnsyncedData(): Boolean

    @Query("SELECT * FROM incomes WHERE userId = :userId AND needsSync = 1")
    suspend fun getUnsyncedIncomes(userId: String): List<IncomeEntity>
    
    @Query("SELECT * FROM incomes WHERE needsSync = 1")
    suspend fun getUnsyncedIncomes(): List<IncomeEntity>
    
    @Query("SELECT DISTINCT userId FROM incomes WHERE needsSync = 1")
    suspend fun getAllUnsyncedUserIds(): List<String>

    @Query("UPDATE incomes SET isSynced = 1, needsSync = 0, lastSyncedAt = :lastSyncedAt WHERE incomeId = :incomeId")
    suspend fun updateSyncStatus(incomeId: String, lastSyncedAt: Long)

    @Query("UPDATE incomes SET needsSync = 1 WHERE incomeId = :incomeId")
    suspend fun markForSync(incomeId: String)

    @Query("UPDATE incomes SET isSynced = 1, needsSync = 0 WHERE incomeId = :incomeId")
    suspend fun markIncomeAsSynced(incomeId: String)

    @Query("SELECT * FROM incomes WHERE isDeleted = 1 AND needsSync = 1")
    suspend fun getUnsyncedDeletedIncomes(): List<IncomeEntity>

    /** Sync timestamp queries
     *  These help in determining what data needs to be synced
     *  between local database and remote server.
     */
    @Query(
        """
    SELECT MIN(createdAt) 
    FROM incomes 
    WHERE userId = :userId AND needsSync = 1
    """
    )
    suspend fun getOldestUnsyncedIncomeTimestamp(userId: String): Long?

    @Query(
        """
    SELECT MAX(lastSyncedAt) 
    FROM incomes 
    WHERE userId = :userId AND lastSyncedAt IS NOT NULL
    """
    )
    suspend fun getLatestSyncedTimestamp(userId: String): Long?

    @Query(
        """
    SELECT MAX(updatedAt) 
    FROM incomes 
    WHERE userId = :userId
    """
    )
    suspend fun getLatestLocalUpdateTimestamp(userId: String): Long?

    @Query(
        """
    SELECT COUNT(*) 
    FROM incomes 
    WHERE userId = :userId AND updatedAt > :timestamp
    """
    )
    suspend fun getUpdatedIncomesSince(userId: String, timestamp: Long): Int

}