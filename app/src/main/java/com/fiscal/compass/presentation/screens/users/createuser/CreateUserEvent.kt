package com.fiscal.compass.presentation.screens.users.createuser

import com.fiscal.compass.domain.model.rbac.Role

sealed class CreateUserEvent {
    data class NameChanged(val name: String) : CreateUserEvent()
    data class EmailChanged(val email: String) : CreateUserEvent()
    data class PasswordChanged(val password: String) : CreateUserEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : CreateUserEvent()
    data class RoleChanged(val role: Role) : CreateUserEvent()
    object CreateUserClicked : CreateUserEvent()
    object ClearError : CreateUserEvent()
    object NavigateBack : CreateUserEvent()
}

