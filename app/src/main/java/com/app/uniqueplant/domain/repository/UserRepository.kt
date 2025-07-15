package com.app.uniqueplant.domain.repository

interface UserRepository {
    suspend fun getUserId(): Long?

    suspend fun getUsername(): String?

    suspend fun getEmail(): String?

    suspend fun isLoggedIn(): Boolean

    suspend fun login(username: String, password: String): Result<Unit>

    suspend fun register(username: String, email: String, password: String): Result<Unit>

    suspend fun logout(): Result<Unit>
    suspend fun addUserToDatabase(userId: String, username: String, email: String, userType: String)
}