package com.fiscal.compass.data.mappers

import com.fiscal.compass.data.local.model.CategoryEntity
import com.fiscal.compass.data.remote.model.CategoryDto
import com.fiscal.compass.domain.model.base.Category
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date


/**
 * Mapper functions to convert between [CategoryEntity] and [Category].
 * These functions are used to convert data between the database layer and the domain layer.
 */
fun CategoryEntity.toDomain(): Category {
    return Category(
        categoryId = this.categoryId,
        name = this.name,
        color = this.color,
        isExpenseCategory = this.isExpenseCategory,
        icon = this.icon,
        description = this.description,
        expectedPersonType = this.expectedPersonType,
        createdAt = Date(this.createdAt),
        updatedAt = Date(this.updatedAt)
    )
}


fun CategoryEntity.toDto(): CategoryDto {
    return CategoryDto(
        categoryId = categoryId,
        name = name,
        color = color,
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        description = description ?: "",
        expectedPersonType = expectedPersonType ?: "",
        icon = icon ?: "",
        isExpenseCategory = isExpenseCategory,
        lastSyncedAt = lastSyncedAt?.let {
            Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt())
        },
        isDeleted = isDeleted,
        updatedAt = Timestamp(updatedAt / 1000, ((updatedAt % 1000) * 1_000_000).toInt())
    )
}

fun CategoryDto.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        categoryId = categoryId,
        name = name,
        color = color,
        isExpenseCategory = isExpenseCategory,
        icon = icon.takeIf { it.isNotBlank() },
        description = description.takeIf { it.isNotBlank() },
        expectedPersonType = expectedPersonType.takeIf { it.isNotBlank() },
        createdAt = createdAt.toDate().time,
        updatedAt = updatedAt.toDate().time,
        isDeleted = isDeleted,
        isSynced = true,
        needsSync = false,
        lastSyncedAt = lastSyncedAt?.toDate()?.time
    )
}


fun CategoryDto.toFirestoreMap(
    syncTime: Long
): Map<String, Any> {
    return mapOf(
        "categoryId" to categoryId,
        "name" to name,
        "color" to color,
        "createdAt" to createdAt,
        "description" to (description.takeIf { it.isNotBlank() } ?: ""),
        "expectedPersonType" to (expectedPersonType.takeIf { it.isNotBlank() } ?: ""),
        "icon" to (icon.takeIf { it.isNotBlank() } ?: ""),
        "isExpenseCategory" to isExpenseCategory,
        "lastSyncedAt" to Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt()),
        "isDeleted" to isDeleted,
        "updatedAt" to Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt())
    )
}

fun Map<String, Any>.toCategoryEntity(): CategoryEntity {
    val createdAtValue = get("createdAt")
    val updatedAtValue = get("updatedAt")
    val lastSyncedAtValue = get("lastSyncedAt")

    // Handle different types for timestamps - could be Timestamp or Long
    val createdAtTime = when (createdAtValue) {
        is Timestamp -> createdAtValue.toDate().time
        is Long -> createdAtValue
        is Number -> createdAtValue.toLong()
        else -> System.currentTimeMillis()
    }

    val updatedAtTime = when (updatedAtValue) {
        is Timestamp -> updatedAtValue.toDate().time
        is Long -> updatedAtValue
        is Number -> updatedAtValue.toLong()
        else -> System.currentTimeMillis()
    }

    val lastSyncedTime = when (lastSyncedAtValue) {
        is Timestamp -> lastSyncedAtValue.toDate().time
        is Long -> lastSyncedAtValue
        is Number -> lastSyncedAtValue.toLong()
        null -> null
        else -> null
    }

    return CategoryEntity(
        categoryId = (get("categoryId") as? String) ?: "",
        name = (get("name") as? String) ?: "",
        color = when (val colorValue = get("color")) {
            is Long -> colorValue.toInt()
            is Int -> colorValue
            is Number -> colorValue.toInt()
            else -> 0
        },
        isExpenseCategory = (get("isExpenseCategory") as? Boolean) ?: false,
        icon = (get("icon") as? String)?.takeIf { it.isNotBlank() },
        description = (get("description") as? String)?.takeIf { it.isNotBlank() },
        expectedPersonType = (get("expectedPersonType") as? String)?.takeIf { it.isNotBlank() },
        createdAt = createdAtTime,
        updatedAt = updatedAtTime,
        isDeleted = (get("isDeleted") as? Boolean) ?: false,
        isSynced = true,
        needsSync = false,
        lastSyncedAt = lastSyncedTime
    )
}


fun DocumentSnapshot.toCategoryDto(): CategoryDto? {
    if (!exists()) return null

    return CategoryDto(
        categoryId = getString("categoryId") ?: "",
        name = getString("name") ?: "",
        color = getLong("color")?.toInt() ?: 0,
        createdAt = getTimestamp("createdAt") ?: Timestamp.now(),
        description = getString("description") ?: "",
        expectedPersonType = getString("expectedPersonType") ?: "",
        icon = getString("icon") ?: "",
        isExpenseCategory = getBoolean("isExpenseCategory") ?: false,
        lastSyncedAt = getTimestamp("lastSyncedAt"),
        updatedAt = getTimestamp("updatedAt") ?: Timestamp.now()
    )
}


/**
 * Converts a [Category] to a [CategoryEntity].
 *
 * @return A [CategoryEntity] representation of the [Category].
 */
fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        categoryId = this.categoryId,
        name = this.name,
        color = this.color,
        isExpenseCategory = this.isExpenseCategory,
        icon = this.icon,
        description = this.description,
        expectedPersonType = this.expectedPersonType,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt.time,
    )
}
