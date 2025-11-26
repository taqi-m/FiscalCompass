package com.fiscal.compass.domain.model.base

import java.util.Date

data class User(
    val userId: String,
    val username: String,
    val email: String,
    val userType: String,
    val passwordHash: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val profilePictureUrl: String? = null,
    val currency: String = "USD",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val lastLoginAt: Date? = null
)