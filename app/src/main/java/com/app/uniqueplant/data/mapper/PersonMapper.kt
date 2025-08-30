package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.PersonEntity
import com.app.uniqueplant.data.model.PersonType
import com.app.uniqueplant.domain.model.Person

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