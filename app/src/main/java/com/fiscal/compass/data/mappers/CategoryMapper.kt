package com.fiscal.compass.data.mappers

import com.fiscal.compass.data.local.model.CategoryEntity
import com.fiscal.compass.data.remote.model.CategoryDto
import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.CategoryTree
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
        parentCategoryId = this.parentCategoryId,
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
        firestoreId = firestoreId ?: "",
        color = color,
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        description = description ?: "",
        expectedPersonType = expectedPersonType ?: "",
        icon = icon ?: "",
        isExpenseCategory = isExpenseCategory,
        isSynced = isSynced,
        lastSyncedAt = lastSyncedAt?.let {
            Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt())
        },
        name = name,
        isDeleted = isDeleted,
        needsSync = needsSync,
        parentCategoryFirestoreId = parentCategoryFirestoreId ?: "",
        updatedAt = Timestamp(updatedAt / 1000, ((updatedAt % 1000) * 1_000_000).toInt())
    )
}

fun CategoryDto.toEntity(): CategoryEntity {
    return CategoryEntity(
        firestoreId = firestoreId.ifBlank { null },
        color = color,
        createdAt = createdAt.toDate().time,
        description = description.ifBlank { null },
        expectedPersonType = expectedPersonType.ifBlank { null },
        icon = icon.ifBlank { null },
        isExpenseCategory = isExpenseCategory,
        isSynced = isSynced,
        lastSyncedAt = lastSyncedAt?.toDate()?.time,
        name = name,
        isDeleted = isDeleted,
        needsSync = needsSync,
        parentCategoryFirestoreId = parentCategoryFirestoreId.ifBlank { null },
        updatedAt = updatedAt.toDate().time
    )
}


fun CategoryDto.toFirestoreMap(
    firestoreId: String,
    firestoreIdOfParent: String? = null,
    syncTime: Long
): Map<String, Any> {
    return mapOf(
        "firestoreId" to firestoreId,
        "color" to color,
        "createdAt" to createdAt,
        "description" to (description.takeIf { it.isNotBlank() } ?: ""),
        "expectedPersonType" to (expectedPersonType.takeIf { it.isNotBlank() } ?: ""),
        "icon" to (icon.takeIf { it.isNotBlank() } ?: ""),
        "isExpenseCategory" to isExpenseCategory,
        "isSynced" to true,
        "lastSyncedAt" to Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt()),
        "name" to name,
        "isDeleted" to isDeleted,
        "needsSync" to false,
        "parentCategoryFirestoreId" to (firestoreIdOfParent ?: ""),
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
        firestoreId = get("firestoreId") as? String,
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
        parentCategoryFirestoreId = (get("parentCategoryFirestoreId") as? String)?.takeIf { it.isNotBlank() },
        createdAt = createdAtTime,
        updatedAt = updatedAtTime,
        isDeleted = (get("isDeleted") as? Boolean) ?: false,
        isSynced = (get("isSynced") as? Boolean) ?: false,
        needsSync = (get("needsSync") as? Boolean) ?: true,
        lastSyncedAt = lastSyncedTime
    )
}


fun DocumentSnapshot.toCategoryDto(): CategoryDto? {
    if (!exists()) return null

    return CategoryDto(
        firestoreId = getString("firestoreId") ?: "",
        color = getLong("color")?.toInt() ?: 0,
        createdAt = getTimestamp("createdAt") ?: Timestamp.now(),
        description = getString("description") ?: "",
        expectedPersonType = getString("expectedPersonType") ?: "",
        icon = getString("icon") ?: "",
        isExpenseCategory = getBoolean("isExpenseCategory") ?: false,
        isSynced = getBoolean("isSynced") ?: false,
        lastSyncedAt = getTimestamp("lastSyncedAt"),
        name = getString("name") ?: "",
        needsSync = getBoolean("needsSync") ?: false,
        parentCategoryFirestoreId = getString("parentCategoryFirestoreId") ?: "",
        updatedAt = getTimestamp("updatedAt") ?: Timestamp.now()
    )
}


/**
 * Converts a [Category] to a [CategoryEntity].
 *
 * @return A [CategoryEntity] representation of the [Category].
 */
fun Category.toCategoryEntity(parentId: Long? = null): CategoryEntity {
    return CategoryEntity(
        categoryId = this.categoryId,
        name = this.name,
        color = this.color,
        isExpenseCategory = this.isExpenseCategory,
        icon = this.icon,
        description = this.description,
        expectedPersonType = this.expectedPersonType,
        parentCategoryId = parentId ?: this.parentCategoryId,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt.time,
    )
}


/**
 * Converts a list of [CategoryEntity] to a [CategoryTree].
 *
 * @return A [CategoryTree] representation of the list of [CategoryEntity].
 */
fun List<CategoryEntity>.toCategoryTree(): CategoryTree {
    val categories = this.map { it.toDomain() }
    val byParent = categories.groupBy { it.parentCategoryId }

    val result = mutableMapOf<Category, List<Category>>()

    categories.filter { it.parentCategoryId == null }.forEach { root ->
        result[root] = buildSubTree(root, byParent)
    }

    result.forEach { category, categories ->
    }

    return result
}

private fun buildSubTree(parent: Category, grouped: Map<Long?, List<Category>>): List<Category> {
    return grouped[parent.categoryId] ?: emptyList()
}


fun Map<Category, List<Category>>.toEntityList(): List<CategoryEntity> {
    val result = mutableListOf<CategoryEntity>()
    var tempId = -1L

    this.forEach { (parent, children) ->
        val parentEntity = parent.toCategoryEntity(parentId = null).copy(categoryId = tempId--)
        result.add(parentEntity)

        children.forEach { child ->
            val childEntity = child.toCategoryEntity(parentId = parentEntity.categoryId)
            result.add(childEntity)
        }
    }
    return result
}
