package com.fiscal.compass.domain.initialization

import android.util.Log
import com.fiscal.compass.data.network.NetworkManager
import com.fiscal.compass.domain.initialization.steps.InitializationStep
import com.fiscal.compass.domain.model.sync.SyncType
import com.fiscal.compass.domain.repository.AuthRepository
import com.fiscal.compass.domain.sync.SyncDependencyManager
import com.fiscal.compass.domain.userBehaviour.UserBehaviorFactory
import com.fiscal.compass.domain.userBehaviour.UserBehaviorStrategy
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.plus

data class InitializationStatus(
    val isInitializing: Boolean = false,
    val currentStep: String? = null,
    val completedSteps: List<SyncType> = emptyList(),
    val pendingSteps: List<SyncType> = emptyList(),
    val progress: Float = 0f,
    val error: String? = null,
    val isCompleted: Boolean = false
)

/**
 * Pure Polymorphic Initialization Manager - ZERO when statements!
 * 
 * Uses:
 * - Strategy Pattern: Gets role-specific behavior from UserBehaviorFactory
 * - Command Pattern: Each InitializationStep executes itself
 * - Polymorphism: Virtual dispatch instead of conditional logic
 * 
 * This class has NO knowledge of specific roles or steps - it just iterates
 * and calls polymorphic methods. TRUE OOP!
 */
@Singleton
class AppInitializationManager @Inject constructor(
    private val behaviorFactory: UserBehaviorFactory,
    private val authRepository: AuthRepository,
    private val dependencyManager: SyncDependencyManager,
    private val networkManager: NetworkManager,
    private val auth: FirebaseAuth
) {

    private lateinit var coroutineScope: CoroutineScope

    private val _initializationStatus = MutableStateFlow(InitializationStatus())
    val initializationStatus: StateFlow<InitializationStatus> = _initializationStatus.asStateFlow()

    fun initialize(scope: CoroutineScope) {
        coroutineScope = scope
    }

    suspend fun initializeApp(): Boolean {
        val userId = auth.currentUser?.uid ?: return false

        if (!networkManager.isOnline()) {
            updateStatus { copy(error = "Network connection required for initialization") }
            return false
        }

        // Check if already initialized
        if (dependencyManager.isInitialized(SyncType.ALL, userId)) {
            updateStatus { copy(isCompleted = true, progress = 1f) }
            return true
        }

        // Get user role - NO when statement here!
        val userRole = authRepository.getUserRole()
        if (userRole == null) {
            Log.e(TAG, "Unable to determine user role")
            updateStatus { copy(error = "Unable to determine user role") }
            return false
        }

        // Get strategy polymorphically (map lookup in factory - NO when!)
        val strategy = try {
            behaviorFactory.getStrategy(userRole)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "No strategy for role: $userRole", e)
            updateStatus { copy(error = "Invalid user role") }
            return false
        }

        Log.d(TAG, "Using strategy for role: $userRole")

        // Check if skip (polymorphic call - NO when!)
        if (strategy.shouldSkipInitialization()) {
            Log.d(TAG, "Initialization skipped for role: $userRole")
            updateStatus {
                copy(
                    isCompleted = true,
                    progress = 1f,
                    currentStep = null
                )
            }
            return true
        }

        updateStatus {
            copy(
                isInitializing = true,
                error = null,
                pendingSteps = dependencyManager.getPendingInitializations(userId)
            )
        }

        // Get steps from strategy (returns different lists per role - NO when!)
        val steps = strategy.getInitializationSteps()

        return try {
            // Execute steps polymorphically - NO when statements!
            executeSteps(steps, userId, strategy)
        } catch (e: Exception) {
            Log.e(TAG, "Initialization failed", e)
            updateStatus {
                copy(
                    isInitializing = false,
                    error = e.message ?: "Initialization failed"
                )
            }
            false
        }
    }

    /**
     * Executes initialization steps using pure polymorphism.
     * NO when statements - just iterates and calls polymorphic methods!
     */
    private suspend fun executeSteps(
        steps: List<InitializationStep>,
        userId: String,
        strategy: UserBehaviorStrategy
    ): Boolean {
        val totalSteps = steps.size
        var completedCount = 0

        updateStatus { copy(isInitializing = true, error = null) }

        try {
            // Pure iteration - NO when statements needed!
            for (step in steps) {
                // Polymorphic call: each step decides if it should run
                if (!step.shouldExecute(userId)) {
                    Log.d(TAG, "Skipping step: ${step.getStepName()}")
                    completedCount++
                    updateStatus { copy(progress = completedCount.toFloat() / totalSteps) }
                    continue
                }

                // Polymorphic call: get step name for UI
                val stepName = step.getStepName()
                updateStatus { copy(currentStep = stepName) }
                Log.d(TAG, "Executing step: $stepName")

                // THE MAGIC: Polymorphic dispatch - NO when statement!
                // Each step knows how to execute itself!
                step.execute(userId)

                // Polymorphic call: each step marks itself complete
                step.markAsComplete(userId)

                // Update progress
                val syncType = step.getSyncType()
                if (syncType != null) {
                    updateStatus {
                        copy(
                            completedSteps = completedSteps + syncType,
                            progress = (++completedCount).toFloat() / totalSteps
                        )
                    }
                } else {
                    completedCount++
                    updateStatus { copy(progress = completedCount.toFloat() / totalSteps) }
                }

                Log.d(TAG, "Completed step: $stepName")
            }

            // Post-initialization hook (polymorphic)
            strategy.onInitializationComplete(userId)

            // Mark all complete
            dependencyManager.markAsInitialized(SyncType.ALL, userId)

            updateStatus {
                copy(
                    isInitializing = false,
                    isCompleted = true,
                    progress = 1f,
                    currentStep = null,
                    pendingSteps = emptyList()
                )
            }

            Log.d(TAG, "Initialization completed successfully")
            return true

        } catch (e: Exception) {
            Log.e(TAG, "Step execution failed", e)
            throw e
        }
    }

    private fun updateStatus(update: InitializationStatus.() -> InitializationStatus) {
        _initializationStatus.value = _initializationStatus.value.update()
    }

    suspend fun retryInitialization(): Boolean {
        return initializeApp()
    }

    fun skipInitialization(userId: String) {
        // Force mark as initialized (for offline scenarios)
        dependencyManager.markAsInitialized(SyncType.CATEGORIES, userId)
        dependencyManager.markAsInitialized(SyncType.PERSONS, userId)
        dependencyManager.markAsInitialized(SyncType.ALL, userId)

        updateStatus {
            copy(
                isInitializing = false,
                isCompleted = true,
                progress = 1f,
                currentStep = null,
                error = null
            )
        }
    }

    companion object {
        private const val TAG = "AppInitManager"
    }
}
