package com.fiscal.compass.data.mappers

import com.fiscal.compass.data.local.model.UserEntity
import com.fiscal.compass.domain.model.base.User
import com.fiscal.compass.domain.model.rbac.Role
import com.google.firebase.Timestamp
import java.util.Date

/**
 * Converts Firestore document data to UserEntity
 */
fun Map<String, Any>.toUserEntity(userId: String): UserEntity? {
    return try {
        val username = this["name"] as? String ?: return null
        val email = this["email"] as? String ?: return null
        val userType = this["userType"] as? String ?: "EMPLOYEE"
        val passwordHash = this["passwordHash"] as? String
        val firstName = this["firstName"] as? String
        val lastName = this["lastName"] as? String
        val profilePictureUrl = this["profilePictureUrl"] as? String
        val currency = this["currency"] as? String ?: "USD"
        
        val createdAt = when (val timestamp = this["createdAt"]) {
            is Timestamp -> timestamp.toDate().time
            is Long -> timestamp
            else -> Date().time
        }
        
        val updatedAt = when (val timestamp = this["updatedAt"]) {
            is Timestamp -> timestamp.toDate().time
            is Long -> timestamp
            else -> Date().time
        }
        
        val lastLoginAt = when (val timestamp = this["lastLoginAt"]) {
            is Timestamp -> timestamp.toDate().time
            is Long -> timestamp
            else -> null
        }
        
        UserEntity(
            userId = userId,
            username = username,
            email = email,
            userType = userType,
            passwordHash = passwordHash,
            firstName = firstName,
            lastName = lastName,
            profilePictureUrl = profilePictureUrl,
            currency = currency,
            createdAt = createdAt,
            updatedAt = updatedAt,
            lastLoginAt = lastLoginAt
        )
    } catch (e: Exception) {
        null
    }
}

/**
 * Converts UserEntity to User domain model
 */
fun UserEntity.toUser(): User {
    return User(
        userId = this.userId,
        username = this.username,
        email = this.email,
        userType = Role.fromString(this.userType),
        passwordHash = this.passwordHash,
        firstName = this.firstName,
        lastName = this.lastName,
        profilePictureUrl = this.profilePictureUrl,
        currency = this.currency,
        createdAt = Date(this.createdAt),
        updatedAt = Date(this.updatedAt),
        lastLoginAt = this.lastLoginAt?.let { Date(it) }
    )
}

/**
 * Converts User domain model to UserEntity
 */
fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = this.userId,
        username = this.username,
        email = this.email,
        userType = this.userType.name,
        passwordHash = this.passwordHash,
        firstName = this.firstName,
        lastName = this.lastName,
        profilePictureUrl = this.profilePictureUrl,
        currency = this.currency,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt.time,
        lastLoginAt = this.lastLoginAt?.time
    )
}

/**
 * Converts UserEntity to Firestore map for uploading
 */
fun UserEntity.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "name" to this.username,
        "email" to this.email,
        "userType" to this.userType,
        "passwordHash" to this.passwordHash,
        "firstName" to this.firstName,
        "lastName" to this.lastName,
        "profilePictureUrl" to this.profilePictureUrl,
        "currency" to this.currency,
        "createdAt" to Timestamp(Date(this.createdAt)),
        "updatedAt" to Timestamp(Date(this.updatedAt)),
        "lastLoginAt" to this.lastLoginAt?.let { Timestamp(Date(it)) }
    )
}

