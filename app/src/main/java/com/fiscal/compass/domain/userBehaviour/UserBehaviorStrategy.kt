package com.fiscal.compass.domain.userBehaviour

import com.fiscal.compass.domain.initialization.steps.InitializationStep
import com.fiscal.compass.domain.sync.strategy.SyncQueryStrategy

/**
 * Strategy interface for user-type-specific behaviors.
 * Different user roles (ADMIN, EMPLOYEE) have different initialization and sync requirements.
 * 
 * This follows the Strategy Pattern to avoid scattered if/when statements throughout the codebase.
 * Uses Command Pattern by returning list of self-executing InitializationStep objects.
 */
interface UserBehaviorStrategy {
    
    /**
     * Returns ordered list of initialization steps to execute.
     * Each step knows how to execute itself (Command Pattern).
     * @return List of InitializationStep objects in execution order
     */
    fun getInitializationSteps(): List<InitializationStep>
    
    /**
     * Determines if this user type can sync data for all users.
     * @return true if user can sync all users' data (typically admin only)
     */
    fun canSyncAllUsers(): Boolean
    
    /**
     * Determines if initialization should be skipped for this user type.
     * Useful for certain user types that don't require full initialization.
     * @return true if initialization can be skipped
     */
    fun shouldSkipInitialization(): Boolean
    
    /**
     * Post-initialization hook called after all steps complete successfully.
     * Allows role-specific logic after initialization.
     * @param userId The user ID that was initialized
     */
    suspend fun onInitializationComplete(userId: String)

    /**
     * Returns the sync query strategy for building permission-based Firestore queries.
     * The strategy evaluates permissions at runtime to handle user switching scenarios.
     * @return SyncQueryStrategy for this user type
     */
    fun getSyncQueryStrategy(): SyncQueryStrategy
}
