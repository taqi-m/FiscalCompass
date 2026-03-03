package com.fiscal.compass.presentation.screens.auth

import com.fiscal.compass.domain.initialization.InitializationStatus

data class AuthScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = "",
    val isLoginSuccess: Boolean = false,
    val initializationStatus: InitializationStatus = InitializationStatus()
)