package com.app.uniqueplant.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "categories",
    indices = [Index("categoryId")],
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    val name: String,
    val color: Int = 0xFF000000.toInt(),
    val isExpenseCategory: Boolean,
    val icon: String? = null,
    val description: String? = null,
    val expectedPersonType: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)