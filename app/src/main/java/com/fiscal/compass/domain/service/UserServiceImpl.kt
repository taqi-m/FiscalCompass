package com.fiscal.compass.domain.service

import com.fiscal.compass.data.mappers.toEntity
import com.fiscal.compass.data.mappers.toUser
import com.fiscal.compass.domain.model.base.User
import com.fiscal.compass.domain.repository.UserRepository
import javax.inject.Inject

class UserServiceImpl @Inject constructor(
    private val userRepository: UserRepository
) : UserService {

    override suspend fun addUser(user: User): Result<Any> {
        return try {
            userRepository.addUser(user.toEntity())
            Result.success("User added successfully.")
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getLoggedInUser(): User? {
        return try {
            userRepository.getLoggedInUser()?.toUser()
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun getUserId(): String? {
        return try {
            userRepository.getUserId()
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun getUsername(): String? {
        return try {
            userRepository.getUsername()
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun getEmail(): String? {
        return try {
            userRepository.getEmail()
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return try {
            userRepository.isLoggedIn()
        } catch (exception: Exception) {
            false
        }
    }

    override suspend fun markAsLoggedIn(userId: String): Result<Any> {
        return try {
            userRepository.markAsLoggedIn(userId)
            Result.success("User marked as logged in.")
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }


    override suspend fun logout(): Result<Any> {
        return try {
            userRepository.logout()
            Result.success("User logged out successfully.")
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun addUserToDatabase(
        userId: String,
        username: String,
        email: String,
        userType: String
    ): Result<Any> {
        return try {
            userRepository.addUserToDatabase(userId, username, email, userType)
            Result.success("User added to database successfully.")
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun syncAllUsersFromFirebase(): Result<List<User>> {
        return try {
            val userEntities = userRepository.syncAllUsersFromFirebase()
            val users = userEntities.map { it.toUser() }
            Result.success(users)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getAllLocalUsers(): List<User> {
        return try {
            userRepository.getAllLocalUsers().map { it.toUser() }
        } catch (exception: Exception) {
            emptyList()
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return try {
            val allUsers = userRepository.getAllLocalUsers()
            allUsers.find { it.userId == userId }?.toUser()
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun updateUser(user: User): Result<Any> {
        return try {
            val userEntity = user.toEntity()
            userRepository.addUser(userEntity) // Room's insert with REPLACE strategy will update
            Result.success("User updated successfully.")
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun deleteUser(user: User): Result<Any> {
        return try {
            // Note: UserRepository doesn't have a delete method yet
            // This would need to be added to UserRepository and UserDao
            Result.failure(Exception("Delete user functionality not yet implemented in repository."))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}