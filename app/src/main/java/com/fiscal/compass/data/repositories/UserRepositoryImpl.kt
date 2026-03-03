package com.fiscal.compass.data.repositories

import com.fiscal.compass.data.local.dao.UserDao
import com.fiscal.compass.data.local.model.UserEntity
import com.fiscal.compass.data.mappers.toUserEntity
import com.fiscal.compass.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore
): UserRepository {

    override suspend fun addUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun getLoggedInUser(): UserEntity? {
        val userId = userDao.getLoggedInUserId() ?: return null
        return userDao.getUserById(userId)
    }

    override suspend fun getUserId(): String? {
        return userDao.getLoggedInUserId()
    }

    override suspend fun getUsername(): String? {
        val userId = userDao.getLoggedInUserId() ?: return null
        return userDao.getUserById(userId)?.username
    }

    override suspend fun getEmail(): String? {
        val userId = userDao.getLoggedInUserId() ?: return null
        return userDao.getUserById(userId)?.email
    }

    override suspend fun isLoggedIn(): Boolean {
        return userDao.getLoggedInUserId() != null
    }

    override suspend fun markAsLoggedIn(userId: String) {
        userDao.markUserAsLoggedIn(userId, System.currentTimeMillis())
    }


    override suspend fun logout() {
        // Clear the lastLoginAt timestamp for the current logged-in user
        val currentUserId = getLoggedInUser()?.userId
        if (currentUserId != null) {
            userDao.clearUserLogin(currentUserId)
        }
    }

    override suspend fun addUserToDatabase(userId: String, username: String, email: String, userType: String) {
        userDao.insertUser(UserEntity(userId, username, email, userType))
    }
    
    override suspend fun syncAllUsersFromFirebase(): List<UserEntity> {
        return try {
            val usersSnapshot = firestore.collection("users")
                .get()
                .await()
            
            val users = usersSnapshot.documents.mapNotNull { doc ->
                doc.data?.toUserEntity(doc.id)
            }
            
            // Insert all users into local database
            if (users.isNotEmpty()) {
                userDao.insertUsers(users)
            }
            
            users
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getAllLocalUsers(): List<UserEntity> {
        return userDao.getAllUsersSync()
    }
    
    override suspend fun insertUsers(users: List<UserEntity>) {
        userDao.insertUsers(users)
    }
}