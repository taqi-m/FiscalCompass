package com.app.uniqueplant.presentation.model

data class PersonUi(
    val personId: Long = 0,
    val name: String = "",
    val personType: String,
    val contact: String? = null,
){
 companion object {
            val dummy = PersonUi(
                personId = 1L,
                name = "John Doe",
                personType = "Customer",
                contact = "john.doe@example.com"
            )

            val dummyList = listOf(
                dummy,
                dummy.copy(personId = 2L, name = "Jane Smith", personType = "Supplier", contact = "jane.smith@example.com"),
                dummy.copy(personId = 3L, name = "Peter Jones", personType = "Employee", contact = null)
            )
        }
}
