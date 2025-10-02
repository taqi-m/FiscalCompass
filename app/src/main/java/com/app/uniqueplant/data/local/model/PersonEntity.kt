package com.app.uniqueplant.data.local.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Keep
@Entity(
    tableName = "persons"
)
data class PersonEntity(
    @PrimaryKey(autoGenerate = true)
    val personId: Long = 0,
    val firestoreId: String? = null,
    val localId: String = UUID.randomUUID().toString(),
    val name: String,
    val personType: PersonType,
    val contact: String? = null,
    // Fields for synchronization
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val needsSync: Boolean = true,
    val lastSyncedAt: Long? = null
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