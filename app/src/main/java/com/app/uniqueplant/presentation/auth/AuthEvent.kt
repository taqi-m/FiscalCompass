package com.app.uniqueplant.presentation.auth

import androidx.navigation.NavHostController
import com.app.uniqueplant.domain.usecase.auth.AuthResult

sealed class AuthEvent {
    data class UsernameChanged(val username: String) : AuthEvent()
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data class LoginSuccess(val appNavController: NavHostController) : AuthEvent()

    object LoginClicked : AuthEvent()
    object SignUpClicked : AuthEvent()
    object SwitchState : AuthEvent()
}