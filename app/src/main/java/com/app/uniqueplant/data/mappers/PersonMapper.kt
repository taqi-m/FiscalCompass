package com.app.uniqueplant.data.mappers

import com.app.uniqueplant.data.local.model.PersonEntity
import com.app.uniqueplant.domain.model.PersonType
import com.app.uniqueplant.data.remote.model.PersonDto
import com.app.uniqueplant.domain.model.base.Person
import com.google.firebase.Timestamp
import java.util.UUID

fun PersonEntity.toDomain(): Person {
    return Person(
        personId = this.personId,
        name = this.name,
        personType = this.personType.name,
        contact = this.contact
    )
}

fun Person.toEntity(): PersonEntity {
    return PersonEntity(
        personId = this.personId,
        name = this.name,
        personType = PersonType.valueOf(this.personType),
        contact = this.contact
    )
}

fun PersonEntity.toDto(): PersonDto {
    return PersonDto(
        firestoreId = firestoreId ?: "",
        name = name,
        personType = personType.name,
        contact = contact ?: "",
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        updatedAt = Timestamp(updatedAt / 1000, ((updatedAt % 1000) * 1_000_000).toInt()),
        isSynced = isSynced,
        needsSync = needsSync,
        lastSyncedAt = lastSyncedAt?.let {
            Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt())
        }
    )
}

fun PersonDto.toFirestoreMap(firestoreId: String, syncTime: Long): Map<String, Any> {
    val map = mutableMapOf<String, Any>(
        "firestoreId" to firestoreId,
        "name" to name,
        "personType" to personType,
        "contact" to (contact.takeIf { it.isNotBlank() } ?: ""),
        "createdAt" to createdAt,
        "updatedAt" to Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt()),
        "isSynced" to true,
        "needsSync" to false,
        "lastSyncedAt" to Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt())
    )
    return map
}

fun Map<String, Any>.toPersonEntity(): PersonEntity {
    val createdAtValue = this["createdAt"]
    val updatedAtValue = this["updatedAt"]
    val lastSyncedAtValue = this["lastSyncedAt"]
    val createdAt = when (createdAtValue) {
        is Timestamp -> createdAtValue
        is Long -> Timestamp(createdAtValue / 1000, ((createdAtValue % 1000) * 1_000_000).toInt())
        else -> Timestamp.now()
    }
    val updatedAt = when (updatedAtValue) {
        is Timestamp -> updatedAtValue
        is Long -> Timestamp(updatedAtValue / 1000, ((updatedAtValue % 1000) * 1_000_000).toInt())
        else -> Timestamp.now()
    }
    val lastSyncedAt = when (lastSyncedAtValue) {
        is Timestamp -> lastSyncedAtValue
        is Long -> Timestamp(
            lastSyncedAtValue / 1000,
            ((lastSyncedAtValue % 1000) * 1_000_000).toInt()
        )

        else -> null
    }

    return PersonEntity(
        personId = 0,
        firestoreId = this["firestoreId"] as? String,
        localId = this["localId"] as? String ?: UUID.randomUUID().toString(),
        name = this["name"] as? String ?: "Unnamed",
        personType = PersonType.valueOf(this["personType"] as? String ?: "OTHER"),
        contact = this["contact"] as? String,
        createdAt = createdAt.toDate().time,
        updatedAt = updatedAt.toDate().time,
        isSynced = this["isSynced"] as? Boolean ?: true,
        needsSync = this["needsSync"] as? Boolean ?: false,
        lastSyncedAt = lastSyncedAt?.toDate()?.time
    )
}