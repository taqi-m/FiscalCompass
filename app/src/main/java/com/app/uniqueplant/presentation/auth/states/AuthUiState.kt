package com.app.uniqueplant.presentation.auth.states

sealed class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    data class Success(val message: String) : AuthUiState()
    data class Error(val error: String) : AuthUiState(errorMessage = error)
    object Loading : AuthUiState(isLoading = true)
    object Idle : AuthUiState()
}