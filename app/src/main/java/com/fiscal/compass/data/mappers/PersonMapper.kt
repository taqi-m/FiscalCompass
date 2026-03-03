package com.fiscal.compass.data.mappers

import com.fiscal.compass.data.local.model.PersonEntity
import com.fiscal.compass.domain.util.PersonType
import com.fiscal.compass.data.remote.model.PersonDto
import com.fiscal.compass.domain.model.base.Person
import com.google.firebase.Timestamp

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
        personId = personId,
        name = name,
        personType = personType.name,
        contact = contact ?: "",
        isDeleted = isDeleted,
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        updatedAt = Timestamp(updatedAt / 1000, ((updatedAt % 1000) * 1_000_000).toInt()),
        lastSyncedAt = lastSyncedAt?.let {
            Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt())
        }
    )
}

fun PersonDto.toPersonEntity(): PersonEntity {
    return PersonEntity(
        personId = personId,
        name = name,
        personType = PersonType.valueOf(personType),
        contact = contact.takeIf { it.isNotBlank() },
        isDeleted = isDeleted,
        createdAt = createdAt.toDate().time,
        updatedAt = updatedAt.toDate().time,
        isSynced = true,
        needsSync = false,
        lastSyncedAt = lastSyncedAt?.toDate()?.time
    )
}