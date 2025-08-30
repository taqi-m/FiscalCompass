package com.app.uniqueplant.presentation.mappers

import com.app.uniqueplant.domain.model.Person
import com.app.uniqueplant.presentation.model.PersonUi

fun Person.toUi(): PersonUi {
    return PersonUi(
        personId = this.personId,
        name = this.name,
        personType = this.personType,
    )
}

fun PersonUi.toDomain(): Person {
    return Person(
        personId = this.personId,
        name = this.name,
        personType = this.personType,
    )
}