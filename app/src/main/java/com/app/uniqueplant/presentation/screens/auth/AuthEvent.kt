package com.app.uniqueplant.presentation.screens.auth

import androidx.navigation.NavHostController

sealed class AuthEvent {
    data class UsernameChanged(val username: String) : AuthEvent()
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data class LoginSuccess(val appNavController: NavHostController) : AuthEvent()

    object LoginClicked : AuthEvent()
    object SignUpClicked : AuthEvent()
    object SwitchState : AuthEvent()

    object RetryInitialization : AuthEvent()
    object SkipInitialization : AuthEvent()
    data class CompleteInitialization(val appNavController: NavHostController) : AuthEvent()
}