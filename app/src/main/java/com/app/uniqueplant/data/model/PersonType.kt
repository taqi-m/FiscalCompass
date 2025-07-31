package com.app.uniqueplant.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "person_types")
data class PersonType(
    @PrimaryKey(autoGenerate = true)
    val typeId: Long,
    val displayName: String
)

