package com.fiscal.compass.data.local.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Keep
@Entity(
    tableName = "expenses",
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
    indices = [Index("categoryId"), Index("userId"), Index("personId")],
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Long = 0,
    val firestoreId: String? = null,  // Firestore document ID
    val localId: String = UUID.randomUUID().toString(), // Unique local identifier
    val amount: Double,
    val amountPaid: Double = 0.0,
    val description: String,
    val date: Long,
    val categoryId: Long,
    val categoryFirestoreId: String? = null,
    val userId: String,
    val personId: Long? = null, // Nullable if not associated with a person
    val personFirestoreId: String? = null,
    val paymentMethod: String? = null,
    val location: String? = null,
    val receipt: String? = null, // URL to receipt image
    val isRecurring: Boolean = false,
    val recurringFrequency: String? = null, // daily, weekly, monthly, yearly
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    // Sync tracking
    val isDeleted: Boolean = false,
    val isSynced: Boolean = false,
    val needsSync: Boolean = true,
    val lastSyncedAt: Long? = null
)