package com.app.uniqueplant.domain.sync

import com.app.uniqueplant.data.managers.SyncType
import com.app.uniqueplant.domain.repository.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

enum class SyncPriority(val level: Int) {
    CRITICAL(0),    // Categories, Persons (must sync first)
    DEPENDENT(1),   // Expenses, Incomes (depend on critical data)
    OPTIONAL(2)     // User profile updates, etc.
}

data class SyncDependency(
    val type: SyncType,
    val priority: SyncPriority,
    val dependencies: List<SyncType> = emptyList()
)

@Singleton
class SyncDependencyManager @Inject constructor(
    private val preferences: PreferenceManager
) {
    companion object {
        private const val PREF_CATEGORIES_INITIALIZED = "categories_initialized"
        private const val PREF_PERSONS_INITIALIZED = "persons_initialized"
        private const val PREF_INCOMES_INITIALIZED = "incomes_initialized"
        private const val PREF_EXPENSES_INITIALIZED = "expenses_initialized"
        private const val PREF_INITIAL_SYNC_COMPLETED = "initial_sync_completed"
    }

    private val syncDependencies = mapOf(
        SyncType.CATEGORIES to SyncDependency(
            type = SyncType.CATEGORIES,
            priority = SyncPriority.CRITICAL
        ),
        SyncType.PERSONS to SyncDependency(
            type = SyncType.PERSONS,
            priority = SyncPriority.CRITICAL
        ),
        SyncType.EXPENSES to SyncDependency(
            type = SyncType.EXPENSES,
            priority = SyncPriority.DEPENDENT,
            dependencies = listOf(SyncType.CATEGORIES, SyncType.PERSONS)
        ),
        SyncType.INCOMES to SyncDependency(
            type = SyncType.INCOMES,
            priority = SyncPriority.DEPENDENT,
            dependencies = listOf(SyncType.CATEGORIES, SyncType.PERSONS)
        )
    )

    fun canSync(syncType: SyncType, userId: String): Boolean {
        val dependency = syncDependencies[syncType] ?: return true

        // Check if all dependencies are satisfied
        return dependency.dependencies.all { depType ->
            isInitialized(depType, userId)
        }
    }

    fun isInitialized(syncType: SyncType, userId: String): Boolean {
        return when (syncType) {
            SyncType.CATEGORIES -> {
                preferences.getBoolean(PREF_CATEGORIES_INITIALIZED, false)
            }

            SyncType.PERSONS -> {
                preferences.getBoolean(PREF_PERSONS_INITIALIZED, false)
            }

            SyncType.EXPENSES -> {
                preferences.getBoolean("${PREF_EXPENSES_INITIALIZED}_$userId", false)
            }

            SyncType.INCOMES -> {
                preferences.getBoolean("${PREF_INCOMES_INITIALIZED}_$userId", false)
            }

            SyncType.ALL -> {
                preferences.getBoolean("${PREF_INITIAL_SYNC_COMPLETED}_$userId", false)
            }
        }
    }

    fun markAsInitialized(syncType: SyncType, userId: String) {
        when (syncType) {
            SyncType.CATEGORIES -> {
                preferences.saveBoolean(PREF_CATEGORIES_INITIALIZED, true)
            }

            SyncType.PERSONS -> {
                preferences.saveBoolean(PREF_PERSONS_INITIALIZED, true)
            }

            SyncType.EXPENSES -> {
                preferences.saveBoolean("${PREF_EXPENSES_INITIALIZED}_$userId", true)
            }

            SyncType.INCOMES -> {
                preferences.saveBoolean("${PREF_INCOMES_INITIALIZED}_$userId", true)
                if (isInitialized(SyncType.EXPENSES, userId)) {
                    preferences.saveBoolean("${PREF_INITIAL_SYNC_COMPLETED}_$userId", true)
                }
            }

            SyncType.ALL -> {
                preferences.saveBoolean("${PREF_INITIAL_SYNC_COMPLETED}_$userId", true)
            }
        }
    }

    fun getRequiredInitializationOrder(): List<SyncType> {
        return listOf(
            SyncType.CATEGORIES,
            SyncType.PERSONS,
            SyncType.EXPENSES,
            SyncType.INCOMES
        )
    }

    fun getPendingInitializations(userId: String): List<SyncType> {
        return getRequiredInitializationOrder().filter { syncType ->
            !isInitialized(syncType, userId)
        }
    }

    fun resetInitialization(userId: String) {
        preferences.remove(PREF_CATEGORIES_INITIALIZED)
        preferences.remove(PREF_PERSONS_INITIALIZED)
        preferences.remove("${PREF_EXPENSES_INITIALIZED}_$userId")
        preferences.remove("${PREF_INCOMES_INITIALIZED}_$userId")
        preferences.remove("${PREF_INITIAL_SYNC_COMPLETED}_$userId")
    }
}