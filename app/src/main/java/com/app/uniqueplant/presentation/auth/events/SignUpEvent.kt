package com.app.uniqueplant.presentation.auth.events

sealed class SignUpEvent {
    data class UsernameChanged(val username: String) : SignUpEvent()
    data class EmailChanged(val email: String) : SignUpEvent()
    data class PasswordChanged(val password: String) : SignUpEvent()
    object SignUpClicked : SignUpEvent()
    object NavigateToLogin : SignUpEvent()
    object ResetState : SignUpEvent()
}
