package com.app.uniqueplant.data.managers

import android.util.Log
import com.app.uniqueplant.data.remote.sync.EnhancedSyncManager
import com.app.uniqueplant.domain.sync.SyncDependencyManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class InitializationStatus(
    val isInitializing: Boolean = false,
    val currentStep: String? = null,
    val completedSteps: List<SyncType> = emptyList(),
    val pendingSteps: List<SyncType> = emptyList(),
    val progress: Float = 0f,
    val error: String? = null,
    val isCompleted: Boolean = false
)

@Singleton
class AppInitializationManager @Inject constructor(
    private val syncManager: EnhancedSyncManager,
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

        updateStatus {
            copy(
                isInitializing = true,
                error = null,
                pendingSteps = dependencyManager.getPendingInitializations(userId)
            )
        }

        try {
            // Step 1: Initialize Categories (Critical)
            if (!dependencyManager.isInitialized(SyncType.CATEGORIES, userId)) {
                updateStatus { copy(currentStep = "Initializing categories...") }

                syncManager.initializeCategories(userId)
                dependencyManager.markAsInitialized(SyncType.CATEGORIES, userId)

                updateStatus {
                    copy(
                        completedSteps = completedSteps + SyncType.CATEGORIES,
                        progress = 0.25f
                    )
                }
            }

            // Step 2: Initialize Persons (Critical)
            if (!dependencyManager.isInitialized(SyncType.PERSONS, userId)) {
                updateStatus { copy(currentStep = "Initializing persons...") }

                syncManager.initializePersons(userId)
                dependencyManager.markAsInitialized(SyncType.PERSONS, userId)

                updateStatus {
                    copy(
                        completedSteps = completedSteps + SyncType.PERSONS,
                        progress = 0.5f
                    )
                }
            }

            // Step 3: Initialize Expenses (Dependent)
            updateStatus { copy(currentStep = "Synchronizing expenses...") }
            syncManager.initializeExpenses(userId)

            updateStatus {
                copy(
                    completedSteps = completedSteps + SyncType.EXPENSES,
                    progress = 0.75f
                )
            }

            // Step 4: Initialize Incomes (Dependent)
            updateStatus { copy(currentStep = "Synchronizing incomes...") }
            syncManager.initializeIncomes(userId)

            updateStatus {
                copy(
                    completedSteps = completedSteps + SyncType.INCOMES,
                    progress = 1f
                )
            }

            // Mark full initialization as complete
            dependencyManager.markAsInitialized(SyncType.ALL, userId)

            updateStatus {
                copy(
                    isInitializing = false,
                    currentStep = null,
                    isCompleted = true,
                    pendingSteps = emptyList()
                )
            }

            return true

        } catch (e: Exception) {
            Log.e("AppInit", "Initialization failed", e)
            updateStatus {
                copy(
                    isInitializing = false,
                    error = e.message ?: "Initialization failed"
                )
            }
            return false
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
}