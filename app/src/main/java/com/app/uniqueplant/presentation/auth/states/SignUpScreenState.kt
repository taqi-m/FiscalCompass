package com.app.uniqueplant.presentation.auth.states

data class SignUpScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)
