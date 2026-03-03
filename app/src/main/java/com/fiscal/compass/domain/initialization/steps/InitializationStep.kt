package com.fiscal.compass.domain.initialization.steps

import com.fiscal.compass.domain.model.sync.SyncType

/**
 * Command Pattern interface for initialization steps.
 * Each step knows how to execute itself, eliminating the need for when statements.
 * 
 * This provides true polymorphism - the caller doesn't need to know which specific
 * step it's executing, just that it implements this interface.
 */
interface InitializationStep {
    
    /**
     * Returns the name of this initialization step for display purposes.
     */
    fun getStepName(): String
    
    /**
     * Returns the SyncType associated with this step, if any.
     */
    fun getSyncType(): SyncType?
    
    /**
     * Checks if this step needs to be executed based on current state.
     * @param userId The user ID to check for
     * @return true if the step should be executed, false if it can be skipped
     */
    suspend fun shouldExecute(userId: String): Boolean
    
    /**
     * Executes the initialization step.
     * @param userId The user ID to initialize for
     * @throws Exception if initialization fails
     */
    suspend fun execute(userId: String)
    
    /**
     * Called after successful execution to mark the step as complete.
     * @param userId The user ID to mark as initialized
     */
    suspend fun markAsComplete(userId: String)
}
