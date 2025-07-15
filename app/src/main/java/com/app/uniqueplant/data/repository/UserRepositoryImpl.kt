package com.app.uniqueplant.data.repository

import com.app.uniqueplant.data.datasource.local.dao.UserDao
import com.app.uniqueplant.data.datasource.preferences.SharedPreferencesRepository
import com.app.uniqueplant.data.model.User
import com.app.uniqueplant.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val prefRepository: SharedPreferencesRepository
): UserRepository {
    override suspend fun getUserId(): Long? {
        TODO("Not yet implemented")
    }

    override suspend fun getUsername(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getEmail(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun isLoggedIn(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun login(
        username: String,
        password: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun addUserToDatabase(
        userId: String,
        username: String,
        email: String,
        userType: String
    ) {
        userDao.insertUser(
            User(
                userId = userId,
                username = username,
                email = email,
                userType = userType
            )
        )
    }

}