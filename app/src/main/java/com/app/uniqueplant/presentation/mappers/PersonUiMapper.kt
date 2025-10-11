package com.app.uniqueplant.presentation.mappers

import com.app.uniqueplant.domain.model.dataModels.Person
import com.app.uniqueplant.presentation.model.PersonUi

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