package com.app.uniqueplant.data.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Keep
@Entity(
    tableName = "categories",
    indices = [Index("categoryId"), Index("parentCategoryId")],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["parentCategoryId"],
            onDelete = ForeignKey.CASCADE, // if parent is deleted → delete all subcategories
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
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

    val parentCategoryId: Long? = null,

    val createdAt: Date = Date(),

    val updatedAt: Date = Date()
)
