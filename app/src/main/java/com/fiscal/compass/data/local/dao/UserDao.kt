package com.fiscal.compass.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fiscal.compass.data.local.model.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(userEntity: UserEntity): Long
    
    @Update
    suspend fun updateUser(userEntity: UserEntity)
    
    @Delete
    suspend fun deleteUser(userEntity: UserEntity)
    
    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT userId FROM users ORDER BY lastLoginAt DESC LIMIT 1")
    suspend fun getLoggedInUserId(): String?
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    @Query("UPDATE users SET lastLoginAt = :loginTime WHERE userId = :userId")
    suspend fun updateLastLogin(userId: String, loginTime: Date)
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("UPDATE users SET lastLoginAt = :loginTime WHERE userId = :userId")
    fun markUserAsLoggedIn(userId: String, loginTime: Long)
}