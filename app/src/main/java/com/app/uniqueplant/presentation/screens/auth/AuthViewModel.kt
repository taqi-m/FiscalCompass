package com.app.uniqueplant.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.app.uniqueplant.data.managers.AppInitializationManager
import com.app.uniqueplant.data.managers.SyncDependencyManager
import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.usecase.auth.LoginUseCase
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import com.app.uniqueplant.domain.usecase.auth.SignUpUseCase
import com.app.uniqueplant.presentation.navigation.MainScreens
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
    private val signUpUseCase: SignUpUseCase,
    private val sessionUseCase: SessionUseCase,
    private val initializationManager: AppInitializationManager,
    private val dependencyManager: SyncDependencyManager
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
                username = "", email = "", password = ""
            )
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.UsernameChanged -> {
                _state.update { it.copy(username = event.username) }
            }

            is AuthEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email) }
            }

            is AuthEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password) }
            }

            is AuthEvent.LoginClicked -> {
                signIn(state.value.email, state.value.password)
            }

            is AuthEvent.SignUpClicked -> {
                signUp(state.value.username, state.value.email, state.value.password)
            }

            is AuthEvent.SwitchState -> {
                _state.update { it.copy(isSignUp = !it.isSignUp) }
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


    fun signUp(username: String, email: String, password: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            signUpUseCase(username, email, password).collect { result ->
                _state.update {
                    when (result) {
                        is Resource.Loading -> it.copy(isLoading = true, error = "")
                        is Resource.Success -> it.copy(
                            isLoading = false, isSuccess = true, error = ""
                        )

                        is Resource.Error -> it.copy(
                            isLoading = false, error = result.message ?: "Sign up failed"
                        )
                    }
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            loginUseCase(email, password).collect { result ->
                _state.update {
                    when (result) {
                        is Resource.Loading -> it.copy(isLoading = true, error = "")
                        is Resource.Success -> {
                            it.copy(
                                isSuccess = true,
                                error = ""
                            )
                        }

                        is Resource.Error -> it.copy(
                            isLoading = false,
                            error = result.message ?: "Login failed"
                        )
                    }
                }
            }
        }
    }


    fun logout() {
        sessionUseCase.logout()
    }

    fun isUserLoggedIn(): Boolean = sessionUseCase.isUserLoggedIn()

}
