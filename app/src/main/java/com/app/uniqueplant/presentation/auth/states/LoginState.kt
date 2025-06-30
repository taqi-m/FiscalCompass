package com.app.uniqueplant.presentation.auth.states

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)