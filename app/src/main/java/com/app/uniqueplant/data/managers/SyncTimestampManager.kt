package com.app.uniqueplant.data.managers

import com.app.uniqueplant.data.local.AppDatabase
import com.app.uniqueplant.data.local.preferences.PreferenceManager
import javax.inject.Inject

class SyncTimestampManager @Inject constructor(
    private val preferences: PreferenceManager,
    private val roomDatabase: AppDatabase
) {
    companion object {
        private const val PREF_LAST_SYNC_EXPENSES = "last_sync_expenses"
        private const val PREF_LAST_SYNC_INCOMES = "last_sync_incomes"
        private const val PREF_LAST_SYNC_CATEGORIES = "last_sync_categories"
        private const val PREF_LAST_SYNC_PERSONS = "last_sync_persons"
        private const val PREF_LAST_FULL_SYNC = "last_full_sync"
        private const val PREF_USER_ID = "sync_user_id"

        // Default to 30 days ago if no sync timestamp exists
        private const val DEFAULT_SYNC_WINDOW_DAYS = 30L
    }

    suspend fun getLastSyncTimestamp(syncType: SyncType, userId: String): Long {
        // Check if user has changed (different user logged in)
        val lastSyncUserId = preferences.getString(PREF_USER_ID, "")
        if (lastSyncUserId != userId) {
            // User changed, reset all timestamps and update user
            resetAllTimestamps()
            preferences.saveString(PREF_USER_ID, userId)
            return getDefaultSyncTimestamp()
        }

        val prefKey = when (syncType) {
            SyncType.EXPENSES -> PREF_LAST_SYNC_EXPENSES
            SyncType.INCOMES -> PREF_LAST_SYNC_INCOMES
            SyncType.CATEGORIES -> PREF_LAST_SYNC_CATEGORIES
            SyncType.PERSONS -> PREF_LAST_SYNC_PERSONS
            SyncType.ALL -> PREF_LAST_FULL_SYNC
        }

        val timestamp = preferences.getLong(prefKey, 0L)

        return if (timestamp == 0L) {
            // No previous sync, use fallback strategy
            getFallbackSyncTimestamp(syncType, userId)
        } else {
            timestamp
        }
    }

    fun getCategoriesLastSyncTimestamp(): Long {
        return preferences.getLong(PREF_LAST_SYNC_CATEGORIES, 0L)
    }

    fun getPersonsLastSyncTimestamp(): Long {
        return preferences.getLong(PREF_LAST_SYNC_PERSONS, 0L)
    }

    fun updateLastSyncTimestamp(syncType: SyncType, timestamp: Long = System.currentTimeMillis()) {
        val prefKey = when (syncType) {
            SyncType.EXPENSES -> PREF_LAST_SYNC_EXPENSES
            SyncType.INCOMES -> PREF_LAST_SYNC_INCOMES
            SyncType.CATEGORIES -> PREF_LAST_SYNC_CATEGORIES
            SyncType.PERSONS -> PREF_LAST_SYNC_PERSONS
            SyncType.ALL -> PREF_LAST_FULL_SYNC
        }

        preferences.saveLong(prefKey, timestamp)

        // Also update the general full sync timestamp
        if (syncType == SyncType.ALL) {
            preferences.saveLong(PREF_LAST_FULL_SYNC, timestamp)
        }
    }

    private suspend fun getFallbackSyncTimestamp(syncType: SyncType, userId: String): Long {
        return when (syncType) {
            SyncType.EXPENSES -> {
                // Get the oldest unsynced expense timestamp, or default window
                getOldestUnsyncedExpenseTimestamp(userId) ?: getDefaultSyncTimestamp()
            }

            SyncType.INCOMES -> {
                getOldestUnsyncedIncomeTimestamp(userId) ?: getDefaultSyncTimestamp()
            }

            SyncType.CATEGORIES -> {
                // Categories are global, sync all
                0L
            }

            SyncType.PERSONS -> {
                getOldestUnsyncedPersonTimestamp(userId) ?: getDefaultSyncTimestamp()
            }

            SyncType.ALL -> {
                // Use the earliest timestamp from all types
                minOf(
                    getFallbackSyncTimestamp(SyncType.EXPENSES, userId),
                    getFallbackSyncTimestamp(SyncType.INCOMES, userId),
                    getFallbackSyncTimestamp(SyncType.PERSONS, userId)
                )
            }
        }
    }

    private fun getDefaultSyncTimestamp(): Long {
        // Default to 30 days ago to avoid downloading too much historical data
        return System.currentTimeMillis() - (DEFAULT_SYNC_WINDOW_DAYS * 24 * 60 * 60 * 1000)
    }

    private suspend fun getOldestUnsyncedExpenseTimestamp(userId: String): Long? {
        return roomDatabase.expenseDao().getOldestUnsyncedExpenseTimestamp(userId)
    }

    private suspend fun getOldestUnsyncedIncomeTimestamp(userId: String): Long? {
        return roomDatabase.incomeDao().getOldestUnsyncedIncomeTimestamp(userId)
    }

    private suspend fun getOldestUnsyncedPersonTimestamp(userId: String): Long? {
        return roomDatabase.personDao().getOldestUnsyncedPersonTimestamp()
    }

    private fun resetAllTimestamps() {
        preferences.remove(PREF_LAST_SYNC_EXPENSES)
        preferences.remove(PREF_LAST_SYNC_INCOMES)
        preferences.remove(PREF_LAST_SYNC_CATEGORIES)
        preferences.remove(PREF_LAST_SYNC_PERSONS)
        preferences.remove(PREF_LAST_FULL_SYNC)

    }

    fun getLastSyncInfo(): SyncInfo {
        val lastFullSync = preferences.getLong(PREF_LAST_FULL_SYNC, 0L)
        val lastExpenseSync = preferences.getLong(PREF_LAST_SYNC_EXPENSES, 0L)
        val lastIncomeSync = preferences.getLong(PREF_LAST_SYNC_INCOMES, 0L)

        return SyncInfo(
            lastFullSync = if (lastFullSync == 0L) null else lastFullSync,
            lastExpenseSync = if (lastExpenseSync == 0L) null else lastExpenseSync,
            lastIncomeSync = if (lastIncomeSync == 0L) null else lastIncomeSync
        )
    }
}

data class SyncInfo(
    val lastFullSync: Long?,
    val lastExpenseSync: Long?,
    val lastIncomeSync: Long?
)