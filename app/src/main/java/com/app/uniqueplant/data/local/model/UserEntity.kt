package com.app.uniqueplant.data.local.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
@Keep
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val username: String,
    val email: String,
    val userType: String,
    val passwordHash: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val profilePictureUrl: String? = null,
    val currency: String = "USD",
    val createdAt: Long = Date().time,
    val updatedAt: Long = Date().time,
    val lastLoginAt: Long? = null
)