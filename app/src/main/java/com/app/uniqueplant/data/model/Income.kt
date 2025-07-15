package com.app.uniqueplant.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "incomes",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId"), Index("userId")]
)
data class Income(
    @PrimaryKey(autoGenerate = true)
    val incomeId: Long = 0,
    val amount: Double,
    val description: String,
    val date: Date,
    val categoryId: Long?,
    val userId: String,
    val source: String? = null,
    val isRecurring: Boolean = false,
    val recurringFrequency: String? = null, // daily, weekly, monthly, yearly
    val isTaxable: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)