package com.app.uniqueplant.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.uniqueplant.data.model.User
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserById(id: String): User?

    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?
    
    @Query("UPDATE users SET lastLoginAt = :loginTime WHERE userId = :userId")
    suspend fun updateLastLogin(userId: String, loginTime: Date)
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
}