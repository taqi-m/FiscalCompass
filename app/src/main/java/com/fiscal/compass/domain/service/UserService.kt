package com.fiscal.compass.domain.service

import com.fiscal.compass.domain.model.base.User

interface UserService {
    
    /**
     * Adds a new user to the database
     */
    suspend fun addUser(user: User): Result<Any>
    
    /**
     * Gets the currently logged-in user
     */
    suspend fun getLoggedInUser(): User?
    
    /**
     * Gets the user ID of the logged-in user
     */
    suspend fun getUserId(): String?
    
    /**
     * Gets the username of the logged-in user
     */
    suspend fun getUsername(): String?
    
    /**
     * Gets the email of the logged-in user
     */
    suspend fun getEmail(): String?
    
    /**
     * Checks if a user is currently logged in
     */
    suspend fun isLoggedIn(): Boolean
    
    /**
     * Marks a user as logged in
     */
    suspend fun markAsLoggedIn(userId: String): Result<Any>
    
    /**
     * Logs out the current user
     */
    suspend fun logout(): Result<Any>
    
    /**
     * Adds a user to the database with the provided details
     */
    suspend fun addUserToDatabase(
        userId: String,
        username: String,
        email: String,
        userType: String
    ): Result<Any>
    
    /**
     * Syncs all users from Firebase to local database
     */
    suspend fun syncAllUsersFromFirebase(): Result<List<User>>
    
    /**
     * Gets all users from local database
     */
    suspend fun getAllLocalUsers(): List<User>
    
    /**
     * Gets a user by their ID
     */
    suspend fun getUserById(userId: String): User?
    
    /**
     * Updates an existing user
     */
    suspend fun updateUser(user: User): Result<Any>
    
    /**
     * Deletes a user from the database
     */
    suspend fun deleteUser(user: User): Result<Any>
}