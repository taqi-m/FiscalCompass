package com.app.uniqueplant.domain.repository

interface UserRepository {
    suspend fun getUserId(): Long?

    suspend fun getUsername(): String?

    suspend fun getEmail(): String?

    suspend fun isLoggedIn(): Boolean

    suspend fun markAsLoggedIn(userId: String)

    suspend fun register(username: String, email: String, password: String)

    suspend fun logout()
    suspend fun addUserToDatabase(userId: String, username: String, email: String, userType: String)
}