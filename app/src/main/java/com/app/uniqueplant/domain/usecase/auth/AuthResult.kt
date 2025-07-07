package com.app.uniqueplant.domain.usecase.auth

data class AuthResult(
    val userId: String,
    val email: String,
    val name: String,
    val userType: String
)