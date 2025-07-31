package com.app.uniqueplant.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "persons",
    foreignKeys = [
        ForeignKey(
            entity = PersonType::class,
            parentColumns = ["typeId"],
            childColumns = ["personTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("personTypeId")]
)
data class Person(
    @PrimaryKey(autoGenerate = true)
    val personId: Long,
    val name: String,
    val contact: String?,
    val personTypeId: Long
)