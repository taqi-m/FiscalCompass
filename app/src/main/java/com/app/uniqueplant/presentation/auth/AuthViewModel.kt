package com.app.uniqueplant.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
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
    private val sessionUseCase: SessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthScreenState())
    val state: StateFlow<AuthScreenState> = _state.asStateFlow()

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

                Log.d("AuthViewModel", "Login Click Triggered")
                signIn(state.value.email, state.value.password)
            }

            is AuthEvent.SignUpClicked -> {
                signUp(state.value.username, state.value.email, state.value.password)
            }

            is AuthEvent.SwitchState -> {
                _state.update { it.copy(isSignUp = !it.isSignUp) }
            }

            is AuthEvent.LoginSuccess -> {
                // Handle login success, e.g., navigate to home screen
                Log.d("AuthViewModel", "Login Success Triggered")
                navigateToHomeScreen(event.appNavController)
            }
        }
    }

    private fun navigateToHomeScreen(appNavController: NavHostController) {
        Log.d("AuthViewModel", "Hone Navigation Triggered")
        viewModelScope.launch {
            sessionUseCase.getUserType().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false) }
                        var route: String = MainScreens.AdminHome.route
                        if (resource.data == "employee") {
                            route = MainScreens.EmployeeHome.route
                        }
                        appNavController.navigate(route) {
                            popUpTo(MainScreens.Auth.route) { inclusive = true }
                        }
                    }

                    is Resource.Error -> {
                        // Handle error case, maybe show a message to the user
                        _state.update { it.copy(error = resource.message ?: "Unknown error") }
                    }

                    is Resource.Loading -> {
                        // Show loading state if needed
                    }
                }
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
                            Log.d("AuthViewModel", "Login Success: ${result.data?.user?.email}")
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
