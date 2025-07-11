package com.app.uniqueplant.presentation.auth

data class AuthScreenState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isSignUp: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)