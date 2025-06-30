package com.app.uniqueplant.presentation.auth.events

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object LoginClicked : LoginEvent()
    object NavigateToSignUp : LoginEvent()
    object ResetState : LoginEvent()
}
