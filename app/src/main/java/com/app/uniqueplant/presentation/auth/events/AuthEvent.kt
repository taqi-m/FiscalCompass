package com.app.uniqueplant.presentation.auth.events

sealed class AuthEvent {
    data class UsernameChanged(val username: String) : AuthEvent()
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()

    object LoginClicked : AuthEvent()
    object SignUpClicked : AuthEvent()
    object SwitchState : AuthEvent()
}
