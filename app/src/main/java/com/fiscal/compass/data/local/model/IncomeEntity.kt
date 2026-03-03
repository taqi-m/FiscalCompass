package com.fiscal.compass.data.local.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Keep
@Entity(
    tableName = "incomes",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["personId"],
            childColumns = ["personId"],
            onDelete = ForeignKey.SET_NULL
        )

    ],
    indices = [Index("categoryId"), Index("userId"), Index("personId")]
)
data class IncomeEntity(
    @PrimaryKey
    val incomeId: String,
    val userId: String,
    val personId: String? = null,
    val source: String? = null,
    val amount: Double,
    val amountPaid: Double = 0.0,
    val description: String,
    val date: Long,
    val categoryId: String,
    val isRecurring: Boolean = false,
    val recurringFrequency: String? = null,
    val isTaxable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    // Sync tracking
    val isDeleted: Boolean = false,
    val isSynced: Boolean = false,
    val needsSync: Boolean = true,
    val lastSyncedAt: Long? = null
)