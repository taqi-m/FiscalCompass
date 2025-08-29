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
    val personType: PersonType,
    val contact: String? = null,
)

enum class PersonType {
    CUSTOMER,
    DEALER,
    EMPLOYEE;

    companion object {
        fun getDefaultTypes(): List<String> {
            return entries.map { it.name }
        }
        fun fromString(type: String): PersonType {
            return entries.firstOrNull { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown person type: $type")
        }
    }
}