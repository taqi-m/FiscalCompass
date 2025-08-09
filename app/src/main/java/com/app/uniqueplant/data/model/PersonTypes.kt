package com.app.uniqueplant.data.model

enum class PersonTypes {
    CUSTOMER,
    DEALER,
    EMPLOYEE;

    companion object {
        fun getDefaultTypes(): List<String> {
            return entries.map { it.name }
        }
    }
}