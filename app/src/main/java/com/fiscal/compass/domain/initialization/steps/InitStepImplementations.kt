package com.fiscal.compass.domain.initialization.steps

import android.util.Log
import com.fiscal.compass.domain.model.sync.SyncType
import com.fiscal.compass.domain.sync.EnhancedSyncManager
import com.fiscal.compass.domain.sync.SyncDependencyManager
import javax.inject.Inject

/**
 * Initialization step for Users.
 * Users are critical data that must be initialized before other data.
 */
class UsersInitStep @Inject constructor(
    private val syncManager: EnhancedSyncManager,
    private val dependencyManager: SyncDependencyManager
) : InitializationStep {
    override fun getStepName(): String = "Initializing users"

    override fun getSyncType(): SyncType = SyncType.USERS


    override suspend fun shouldExecute(userId: String): Boolean {
        return !dependencyManager.isInitialized(SyncType.USERS, userId)
    }

    override suspend fun execute(userId: String) {
        Log.d(TAG, "Executing: ${getStepName()}")
        syncManager.initializeUsers(userId)
    }

    override suspend fun markAsComplete(userId: String) {
        dependencyManager.markAsInitialized(SyncType.USERS, userId)
    }

    companion object {
        private const val TAG = "UsersInitStep"
    }

}

/**
 * Initialization step for Categories.
 * Categories are critical data that must be initialized before dependent data.
 */
class CategoriesInitStep @Inject constructor(
    private val syncManager: EnhancedSyncManager,
    private val dependencyManager: SyncDependencyManager
) : InitializationStep {
    
    override fun getStepName(): String = "Initializing categories"
    
    override fun getSyncType(): SyncType = SyncType.CATEGORIES
    
    override suspend fun shouldExecute(userId: String): Boolean {
        return !dependencyManager.isInitialized(SyncType.CATEGORIES, userId)
    }
    
    override suspend fun execute(userId: String) {
        Log.d(TAG, "Executing: ${getStepName()}")
        syncManager.initializeCategories()
    }
    
    override suspend fun markAsComplete(userId: String) {
        dependencyManager.markAsInitialized(SyncType.CATEGORIES, userId)
    }
    
    companion object {
        private const val TAG = "CategoriesInitStep"
    }
}

/**
 * Initialization step for Persons.
 * Persons are critical data needed before transactions can be initialized.
 */
class PersonsInitStep @Inject constructor(
    private val syncManager: EnhancedSyncManager,
    private val dependencyManager: SyncDependencyManager
) : InitializationStep {
    
    override fun getStepName(): String = "Initializing persons"
    
    override fun getSyncType(): SyncType = SyncType.PERSONS
    
    override suspend fun shouldExecute(userId: String): Boolean {
        return !dependencyManager.isInitialized(SyncType.PERSONS, userId)
    }
    
    override suspend fun execute(userId: String) {
        Log.d(TAG, "Executing: ${getStepName()}")
        syncManager.initializePersons(userId)
    }
    
    override suspend fun markAsComplete(userId: String) {
        dependencyManager.markAsInitialized(SyncType.PERSONS, userId)
    }
    
    companion object {
        private const val TAG = "PersonsInitStep"
    }
}

/**
 * Initialization step for Expenses.
 * Expenses depend on Categories and Persons being initialized first.
 */
class ExpensesInitStep @Inject constructor(
    private val syncManager: EnhancedSyncManager
) : InitializationStep {
    
    override fun getStepName(): String = "Synchronizing expenses"
    
    override fun getSyncType(): SyncType = SyncType.EXPENSES
    
    override suspend fun shouldExecute(userId: String): Boolean {
        // Expenses are always synchronized (dependency manager handles prerequisites)
        return true
    }
    
    override suspend fun execute(userId: String) {
        Log.d(TAG, "Executing: ${getStepName()}")
        syncManager.initializeExpenses(userId)
    }
    
    override suspend fun markAsComplete(userId: String) {
        // Expenses don't need persistent initialization flag
        // They sync on every app start if needed
    }
    
    companion object {
        private const val TAG = "ExpensesInitStep"
    }
}

/**
 * Initialization step for Incomes.
 * Incomes depend on Categories and Persons being initialized first.
 */
class IncomesInitStep @Inject constructor(
    private val syncManager: EnhancedSyncManager
) : InitializationStep {
    
    override fun getStepName(): String = "Synchronizing incomes"
    
    override fun getSyncType(): SyncType = SyncType.INCOMES
    
    override suspend fun shouldExecute(userId: String): Boolean {
        // Incomes are always synchronized (dependency manager handles prerequisites)
        return true
    }
    
    override suspend fun execute(userId: String) {
        Log.d(TAG, "Executing: ${getStepName()}")
        syncManager.initializeIncomes(userId)
    }
    
    override suspend fun markAsComplete(userId: String) {
        // Incomes don't need persistent initialization flag
        // They sync on every app start if needed
    }
    
    companion object {
        private const val TAG = "IncomesInitStep"
    }
}
