package com.app.uniqueplant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "persons"
)
data class PersonEntity(
    @PrimaryKey(autoGenerate = true)
    val personId: Long = 0,
    val name: String,
    val personType: String,
    val contact: String? = null,
)