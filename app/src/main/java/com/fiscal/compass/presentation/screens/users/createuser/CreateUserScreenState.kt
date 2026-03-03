package com.fiscal.compass.presentation.screens.users.createuser

import com.fiscal.compass.domain.model.rbac.Role

data class CreateUserScreenState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedRole: Role = Role.EMPLOYEE,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val createdUserId: String? = null
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                password.length >= 6 &&
                password == confirmPassword

    val passwordError: String?
        get() = when {
            password.isNotBlank() && password.length < 6 -> "Password must be at least 6 characters"
            confirmPassword.isNotBlank() && password != confirmPassword -> "Passwords don't match"
            else -> null
        }
}

