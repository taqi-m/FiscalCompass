package com.fiscal.compass.presentation.mappers

import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.presentation.model.PersonUi

fun Person.toUi(): PersonUi {
    return PersonUi(
        personId = this.personId,
        name = this.name,
        personType = this.personType,
    )
}

fun List<Person>.toUiList(): List<PersonUi> {
    return this.map { it.toUi() }
}

fun PersonUi.toDomain(): Person {
    return Person(
        personId = this.personId,
        name = this.name,
        personType = this.personType,
    )
}