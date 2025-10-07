package com.app.uniqueplant.domain.repository

import com.app.uniqueplant.data.local.model.UserEntity

interface UserRepository {

    suspend fun addUser(user: UserEntity)

    suspend fun getLoggedInUser(): UserEntity?

    suspend fun getUserId(): String?

    suspend fun getUsername(): String?

    suspend fun getEmail(): String?

    suspend fun isLoggedIn(): Boolean

    suspend fun markAsLoggedIn(userId: String)

    suspend fun register(username: String, email: String, password: String)

    suspend fun logout()
    suspend fun addUserToDatabase(userId: String, username: String, email: String, userType: String)
}