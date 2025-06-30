package com.app.uniqueplant.presentation.auth

sealed class AuthResult(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
    object Idle : AuthResult()
}