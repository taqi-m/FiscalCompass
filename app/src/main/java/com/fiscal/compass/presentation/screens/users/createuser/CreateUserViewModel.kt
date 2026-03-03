package com.fiscal.compass.presentation.screens.users.createuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.model.Resource
import com.fiscal.compass.domain.model.rbac.Permission
import com.fiscal.compass.domain.model.rbac.Role
import com.fiscal.compass.domain.service.analytics.AnalyticsService
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import com.fiscal.compass.domain.usecase.user.CreateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val _state = MutableStateFlow(CreateUserScreenState())
    val state: StateFlow<CreateUserScreenState> = _state.asStateFlow()

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    init {
        checkManageUsersPermission()
    }

    private fun checkManageUsersPermission() {
        viewModelScope.launch {
            _hasPermission.value = checkPermissionUseCase(Permission.MANAGE_USERS)
        }
    }

    fun onEvent(event: CreateUserEvent) {
        when (event) {
            is CreateUserEvent.NameChanged -> {
                _state.update { it.copy(name = event.name) }
            }

            is CreateUserEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email) }
            }

            is CreateUserEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password) }
            }

            is CreateUserEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.confirmPassword) }
            }

            is CreateUserEvent.RoleChanged -> {
                _state.update { it.copy(selectedRole = event.role) }
            }

            CreateUserEvent.CreateUserClicked -> {
                createUser()
            }

            CreateUserEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }

            CreateUserEvent.NavigateBack -> {
                // Navigation handled by the screen
            }
        }
    }

    private fun createUser() {
        val currentState = _state.value

        if (!currentState.isFormValid) {
            _state.update { it.copy(error = "Please fill all fields correctly") }
            return
        }

        viewModelScope.launch {
            createUserUseCase(
                name = currentState.name,
                email = currentState.email,
                password = currentState.password,
                role = currentState.selectedRole
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                createdUserId = result.data,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Failed to create user"
                            )
                        }
                    }
                }
            }
        }
    }

    fun resetState() {
        _state.value = CreateUserScreenState()
    }
}

