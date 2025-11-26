package com.fiscal.compass.data.local.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Keep
@Entity(
    tableName = "categories",
    indices = [Index("categoryId"), Index("parentCategoryId")],
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["categoryId"],
        childColumns = ["parentCategoryId"],
        onDelete = ForeignKey.CASCADE, // if parent is deleted → delete all subcategories
        onUpdate = ForeignKey.NO_ACTION
    )]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    val firestoreId: String? = null,
    val localId: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Int = 0xFF000000.toInt(),
    val isExpenseCategory: Boolean,
    val icon: String? = null,
    val description: String? = null,
    val expectedPersonType: String? = null,
    val parentCategoryId: Long? = null,
    val parentCategoryFirestoreId: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    // Sync tracking
    val isDeleted: Boolean = false,
    val isSynced: Boolean = false,
    val needsSync: Boolean = true,
    val lastSyncedAt: Long? = null
)
