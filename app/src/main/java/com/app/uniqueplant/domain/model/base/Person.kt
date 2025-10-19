package com.app.uniqueplant.domain.model.base

import com.app.uniqueplant.domain.model.PersonType

data class Person(
    val personId: Long = 0,
    val name: String,
    val personType: String,
    val contact: String? = null,

) {
    companion object {
        val dummy = Person(
            personId = 1L,
            name = "John Doe",
            personType = PersonType.CUSTOMER.name,
            contact = "john.doe@example.com"
        )

        val dummyList = listOf(
            dummy,
            dummy.copy(personId = 2L, name = "Jane Smith", personType = PersonType.EMPLOYEE.name, contact = "jane.smith@example.com"),
            dummy.copy(personId = 3L, name = "Peter Jones", personType = PersonType.DEALER.name , contact = null)
        )
    }
}