package com.fiscal.compass.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.fiscal.compass.domain.initialization.AppInitializationManager
import com.fiscal.compass.domain.sync.SyncDependencyManager
import com.fiscal.compass.domain.model.Resource
import com.fiscal.compass.domain.service.analytics.AnalyticsEvent
import com.fiscal.compass.domain.service.analytics.AnalyticsService
import com.fiscal.compass.domain.service.crashlytics.CrashlyticsService
import com.fiscal.compass.domain.usecase.auth.LoginUseCase
import com.fiscal.compass.domain.usecase.auth.SessionUseCase
import com.fiscal.compass.presentation.navigation.MainScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sessionUseCase: SessionUseCase,
    private val initializationManager: AppInitializationManager,
    private val dependencyManager: SyncDependencyManager,
    private val analyticsService: AnalyticsService,
    private val crashlyticsService: CrashlyticsService
) : ViewModel() {

    val initializationStatus = initializationManager.initializationStatus
    private val _state = MutableStateFlow(AuthScreenState())

    val state: StateFlow<AuthScreenState> = _state.asStateFlow()

    init {
        // Initialize the initialization manager
        initializationManager.initialize(viewModelScope)
    }

    fun resetFields() {
        _state.update {
            it.copy(
                email = "", password = ""
            )
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email) }
            }

            is AuthEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password) }
            }

            is AuthEvent.LoginClicked -> {
                signIn(state.value.email, state.value.password)
            }


            is AuthEvent.LoginSuccess -> {
                viewModelScope.launch { initializeApp(event.appNavController) }
            }

            is AuthEvent.CompleteInitialization -> {
                navigateToHome(event.appNavController)
            }

            AuthEvent.RetryInitialization -> {
                retryInitialization()
            }

            AuthEvent.SkipInitialization -> {
                skipInitialization()
            }
        }
    }


    private fun navigateToHome(appNavController: NavHostController) {
        viewModelScope.launch {
            sessionUseCase.getUserType().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        var route: String = MainScreens.AdminHome.route
                        /*if (resource.data == "employee") {
                            route = MainScreens.EmployeeHome.route
                        }*/
                        appNavController.navigate(route) {
                            popUpTo(appNavController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    is Resource.Error -> {
                        // Handle error case, maybe show a message to the user
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message ?: "Unknown error"
                            )
                        }
                    }

                    is Resource.Loading -> {
                        // Show loading state if needed
                    }
                }
            }
        }
    }


    private suspend fun initializeApp(appNavController: NavHostController) {
        _state.update { it.copy(isLoginSuccess = true) }
        // Start initialization after successful login
        val initSuccess = initializationManager.initializeApp()
        if (initSuccess) {
            _state.update { it.copy(isLoading = false) }
            navigateToHome(appNavController)
        } else {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = initializationStatus.value.error ?: "Initialization failed"
                )
            }
        }
    }

    // Add methods to handle initialization
    fun retryInitialization() {
        viewModelScope.launch {
            initializationManager.retryInitialization()
        }
    }

    fun skipInitialization() {
        viewModelScope.launch {
            sessionUseCase.getCurrentUser()?.uid?.let { userId ->
                initializationManager.skipInitialization(userId)
            }
        }
    }


    fun signIn(email: String, password: String) {
        _state.update { it.copy(isLoading = true) }
        analyticsService.logEvent(AnalyticsEvent.LoginStarted)
        viewModelScope.launch {
            val loginResult = loginUseCase(email, password)
            handleResult(loginResult)
        }
    }

    private fun handleResult(result: Result<String>) {
        if (result.isSuccess) {
            val userId = result.getOrNull()
            analyticsService.logEvent(AnalyticsEvent.LoginSuccess())
            analyticsService.setUserId(userId)
            crashlyticsService.setUserId(userId)
            _state.update {
                it.copy(
                    isLoading = false,
                    isSuccess = true,
                    isLoginSuccess = true,
                    error = ""
                )
            }
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "Login failed"
            analyticsService.logEvent(AnalyticsEvent.LoginFailed(errorMsg))
            crashlyticsService.log("Login failed: $errorMsg")
            _state.update {
                it.copy(
                    isLoading = false,
                    isSuccess = false,
                    error = errorMsg
                )
            }
        }
    }


    fun logout() {
        analyticsService.logEvent(AnalyticsEvent.Logout)
        analyticsService.setUserId(null)
        crashlyticsService.setUserId(null)
        sessionUseCase.logout()
    }

    fun isUserLoggedIn(): Boolean = sessionUseCase.isUserLoggedIn()

}
