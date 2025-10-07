package com.app.uniqueplant.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.uniqueplant.data.local.model.IncomeEntity
import com.app.uniqueplant.data.local.model.IncomeFullDbo
import com.app.uniqueplant.data.local.model.IncomeWithCategoryDbo
import com.app.uniqueplant.data.local.model.IncomeWithPersonDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(incomeEntity: IncomeEntity): Long

    @Update
    suspend fun update(incomeEntity: IncomeEntity)

    @Delete
    suspend fun delete(incomeEntity: IncomeEntity)

    @Query("SELECT * FROM incomes")
    fun getAll(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY date DESC")
    fun getAllByUser(userId: String): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE  categoryId = :categoryId ORDER BY date DESC")
    fun getAllByCategory(categoryId: Long): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE personId = :personId ORDER BY date DESC")
    fun getAllByPerson(personId: Long): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getAllByDateRange(startDate: Long, endDate: Long): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE incomeId = :id")
    suspend fun getById(id: Long): IncomeEntity?

    @Query("SELECT SUM(amount) FROM incomes WHERE userId = :userId")
    fun getSumByUser(userId: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM incomes WHERE date BETWEEN :startDate AND :endDate")
    fun getSumByDateRange(startDate: Long, endDate: Long): Flow<Double>

    @Query("SELECT categoryId, SUM(amount) as total FROM incomes WHERE userId = :userId GROUP BY categoryId ORDER BY total DESC")
    fun getSumByCategory(userId: String): Flow<Map<@MapColumn("categoryId") Long?, @MapColumn("total") Double>>

    @Query("SELECT SUM(amount) FROM incomes WHERE personId = :personId")
    fun getSumByPerson(personId: Long): Flow<Double>

    @Transaction
    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY date DESC")
    fun getIncomesWithCategory(userId: String): Flow<List<IncomeWithCategoryDbo>>

    @Transaction
    @Query("SELECT * FROM incomes WHERE personId = :personId ORDER BY date DESC")
    fun getIncomesWithPerson(personId: String): Flow<List<IncomeWithPersonDbo>>

    @Transaction
    @Query("SELECT * FROM incomes WHERE incomeId = :id ORDER BY date DESC LIMIT 1")
    suspend fun getSingleFullIncome(id: Long): IncomeFullDbo

    @Query(
        """
        SELECT * FROM incomes
        WHERE (:userIds IS NULL OR userId IN (:userIds))
          AND (:personIds IS NULL OR personId IN (:personIds))
          AND (:categoryIds IS NULL OR categoryId IN (:categoryIds))
          AND (
              (:startDate IS NULL AND :endDate IS NULL)
              OR (:startDate IS NOT NULL AND :endDate IS NULL AND date >= :startDate)
              OR (:startDate IS NULL AND :endDate IS NOT NULL AND date <= :endDate)
              OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND date BETWEEN :startDate AND :endDate)
          )
    """
    )
    fun getAllFiltered(
        userIds: List<String>? = null,          // nullable to ignore
        personIds: List<Long>? = null,   // pass null to ignore
        categoryIds: List<Long>? = null, // pass null to ignore
        startDate: Long? = null,         // nullable → open start
        endDate: Long?  = null            // nullable → open end
    ): Flow<List<IncomeEntity>>

    @Query("SELECT COUNT(*) FROM incomes WHERE userId = :userId")
    suspend fun getIncomeCount(userId: String): Int

    /**
     * * Sync-related queries
     */

    @Query("UPDATE incomes SET isDeleted = 1, needsSync = 1, updatedAt = :timestamp WHERE incomeId = :incomeId")
    suspend fun markAsDeleted(incomeId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM incomes WHERE needsSync = 1")
    fun getUnsyncedIncomeCount(): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM incomes WHERE needsSync = 1)")
    suspend fun hasUnsyncedData(): Boolean

    @Query("SELECT * FROM incomes WHERE userId = :userId AND needsSync = 1")
    suspend fun getUnsyncedIncomes(userId: String): List<IncomeEntity>

    @Query("SELECT * FROM incomes WHERE localId = :localId")
    suspend fun getIncomeByLocalId(localId: String): IncomeEntity?

    @Query(
        """
        UPDATE incomes 
        SET firestoreId = :firestoreId, isSynced = :isSynced, needsSync = 0, lastSyncedAt = :lastSyncedAt 
        WHERE incomeId = :incomeId
    """
    )
    suspend fun updateSyncStatus(incomeId: Long, firestoreId: String, isSynced: Boolean, lastSyncedAt: Long)

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