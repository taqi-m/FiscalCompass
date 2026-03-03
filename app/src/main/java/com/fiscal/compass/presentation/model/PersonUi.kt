package com.fiscal.compass.presentation.model

import com.fiscal.compass.domain.util.PersonType

data class PersonUi(
    val personId: String = "",
    val name: String = "",
    val personType: String,
    val contact: String? = null,
){
 companion object {
            val dummy = PersonUi(
                personId = "1",
                name = "John Doe",
                personType = PersonType.CUSTOMER.name,
                contact = "john.doe@example.com"
            )

            val dummyList = listOf(
                dummy,
                dummy.copy(personId = "2", name = "Jane Smith", personType = PersonType.EMPLOYEE.name, contact = "jane.smith@example.com"),
                dummy.copy(personId = "3", name = "Peter Jones", personType = PersonType.DEALER.name , contact = null)
            )
        }
}
