package com.app.uniqueplant.data.repositories

import com.app.uniqueplant.data.local.model.UserEntity
import com.app.uniqueplant.data.local.dao.UserDao
import com.app.uniqueplant.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
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

    override suspend fun markAsLoggedIn(userId: String) {
        userDao.markUserAsLoggedIn(userId, System.currentTimeMillis())
    }

    override suspend fun register(username: String, email: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun addUserToDatabase(userId: String, username: String, email: String, userType: String) {
        userDao.insertUser(UserEntity(userId, username, email, userType))
    }
}