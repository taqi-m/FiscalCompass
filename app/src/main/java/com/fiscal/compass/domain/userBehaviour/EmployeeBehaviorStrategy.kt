package com.fiscal.compass.domain.userBehaviour

import android.util.Log
import com.fiscal.compass.domain.initialization.steps.CategoriesInitStep
import com.fiscal.compass.domain.initialization.steps.ExpensesInitStep
import com.fiscal.compass.domain.initialization.steps.IncomesInitStep
import com.fiscal.compass.domain.initialization.steps.InitializationStep
import com.fiscal.compass.domain.sync.strategy.SyncQueryStrategy
import javax.inject.Inject

/**
 * Behavior strategy for EMPLOYEE users.
 * 
 * Employees have limited access:
 * - Initialize only categories (read-only global data)
 * - Initialize only their own expenses and incomes
 * - Cannot sync all users' data
 * - Persons initialization skipped (employees don't manage persons)
 * 
 * Uses Composition: Composes only the steps that employees need.
 */
class EmployeeBehaviorStrategy @Inject constructor(
    private val categoriesStep: CategoriesInitStep,
    private val expensesStep: ExpensesInitStep,
    private val incomesStep: IncomesInitStep,
    private val syncQueryStrategy: SyncQueryStrategy
    // NOTE: NO personsStep - employees don't initialize persons
) : UserBehaviorStrategy {

    override fun getInitializationSteps(): List<InitializationStep> {
        // Employees skip Persons initialization
        // They only need: Categories → Expenses → Incomes
        return listOf(
            categoriesStep,
            expensesStep,
            incomesStep
        )
    }

    override fun canSyncAllUsers(): Boolean {
        // Employees cannot sync all users' data
        return false
    }

    override fun shouldSkipInitialization(): Boolean {
        // Employees require initialization
        return false
    }

    override suspend fun onInitializationComplete(userId: String) {
        // Employee-specific post-initialization logic
        Log.d(TAG, "Employee initialization completed for user: $userId")
        // Future: Could trigger employee-specific analytics here
    }

    override fun getSyncQueryStrategy(): SyncQueryStrategy {
        return syncQueryStrategy
    }

    companion object {
        private const val TAG = "EmployeeBehaviorStrategy"
    }
}
