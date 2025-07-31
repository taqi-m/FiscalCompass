package com.app.uniqueplant.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = PersonType::class,
            parentColumns = ["typeId"],
            childColumns = ["expectedPersonTypeId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("expectedPersonTypeId")],
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    val name: String,
    val color: Int,
    val isExpenseCategory: Boolean,
    val icon: String? = null,
    val description: String? = null,
    val expectedPersonTypeId: Long? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)