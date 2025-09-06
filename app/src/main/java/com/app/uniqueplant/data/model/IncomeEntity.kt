package com.app.uniqueplant.data.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
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
    @PrimaryKey(autoGenerate = true)
    val incomeId: Long = 0,
    val amount: Double,
    val description: String,
    val date: Date,
    val categoryId: Long,
    val userId: String,
    val personId: Long? = null,
    val source: String? = null,
    val isRecurring: Boolean = false,
    val recurringFrequency: String? = null,
    val isTaxable: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)