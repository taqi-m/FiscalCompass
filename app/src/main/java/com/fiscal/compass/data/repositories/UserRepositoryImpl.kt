package com.fiscal.compass.data.repositories

import com.fiscal.compass.data.local.model.UserEntity
import com.fiscal.compass.data.local.dao.UserDao
import com.fiscal.compass.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
): UserRepository {

    override suspend fun addUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun getLoggedInUser(): UserEntity? {
        val userId = userDao.getLoggedInUserId() ?: return null
        return userDao.getUserById(userId)
    }

    override suspend fun getUserId(): String? {
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