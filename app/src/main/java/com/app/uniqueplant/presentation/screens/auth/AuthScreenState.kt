package com.app.uniqueplant.presentation.screens.auth

import com.app.uniqueplant.data.manager.InitializationStatus

data class AuthScreenState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isSignUp: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = "",
    val isLoginSuccess: Boolean = false,
    val initializationStatus: InitializationStatus = InitializationStatus()
)