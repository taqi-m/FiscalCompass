package com.fiscal.compass.domain.repository

import com.fiscal.compass.data.local.model.UserEntity

interface UserRepository {

    suspend fun addUser(user: UserEntity)

    suspend fun getLoggedInUser(): UserEntity?

    suspend fun getUserId(): String?

    suspend fun getUsername(): String?

    suspend fun getEmail(): String?

    suspend fun isLoggedIn(): Boolean

    suspend fun markAsLoggedIn(userId: String)


    suspend fun logout()
    
    suspend fun addUserToDatabase(userId: String, username: String, email: String, userType: String)
    
    /**
     * Fetches all users from Firebase Firestore and stores them in local database.
     * This is typically called during app initialization in admin mode.
     * 
     * @return List of UserEntity objects that were synced
     */
    suspend fun syncAllUsersFromFirebase(): List<UserEntity>
    
    /**
     * Gets all users from local database
     * 
     * @return List of all UserEntity objects in local database
     */
    suspend fun getAllLocalUsers(): List<UserEntity>
    
    /**
     * Inserts multiple users into local database
     * 
     * @param users List of UserEntity objects to insert
     */
    suspend fun insertUsers(users: List<UserEntity>)
}