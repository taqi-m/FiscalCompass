package com.fiscal.compass.domain.userBehaviour

import android.util.Log
import com.fiscal.compass.domain.initialization.steps.CategoriesInitStep
import com.fiscal.compass.domain.initialization.steps.ExpensesInitStep
import com.fiscal.compass.domain.initialization.steps.IncomesInitStep
import com.fiscal.compass.domain.initialization.steps.InitializationStep
import com.fiscal.compass.domain.initialization.steps.PersonsInitStep
import com.fiscal.compass.domain.initialization.steps.UsersInitStep
import com.fiscal.compass.domain.sync.strategy.SyncQueryStrategy
import javax.inject.Inject

/**
 * Behavior strategy for ADMIN users.
 * 
 * Admins have full access to:
 * - All initialization steps (categories, persons, expenses, incomes)
 * - Can sync all users' data if they have the permission
 * - Full system initialization required
 * 
 * Uses Composition: Composes individual step objects rather than implementing logic directly.
 */
class AdminBehaviorStrategy @Inject constructor(
    private val usersStep: UsersInitStep,
    private val categoriesStep: CategoriesInitStep,
    private val personsStep: PersonsInitStep,
    private val expensesStep: ExpensesInitStep,
    private val incomesStep: IncomesInitStep,
    private val syncQueryStrategy: SyncQueryStrategy
) : UserBehaviorStrategy {

    override fun getInitializationSteps(): List<InitializationStep> {
        // Admins get all steps, in specific order
        // Order matters: Categories & Persons must be before Expenses & Incomes
        return listOf(
            usersStep,
            categoriesStep,
            personsStep,
            expensesStep,
            incomesStep
        )
    }

    override fun canSyncAllUsers(): Boolean {
        // Admins can sync all users' data
        return true
    }

    override fun shouldSkipInitialization(): Boolean {
        // Admins require full initialization
        return false
    }

    override suspend fun onInitializationComplete(userId: String) {
        // Admin-specific post-initialization logic
        Log.d(TAG, "Admin initialization completed for user: $userId")
        // Future: Could trigger admin-specific sync or analytics here
    }

    override fun getSyncQueryStrategy(): SyncQueryStrategy {
        return syncQueryStrategy
    }

    companion object {
        private const val TAG = "AdminBehaviorStrategy"
    }
}
