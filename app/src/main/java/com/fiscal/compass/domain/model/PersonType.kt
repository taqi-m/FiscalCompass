package com.fiscal.compass.domain.model

enum class PersonType {
    CUSTOMER,
    DEALER,
    EMPLOYEE;

    companion object {
        fun getDefaultTypes(): List<String> {
            return entries.map { it.name }
        }
        fun fromString(type: String): PersonType {
            return entries.firstOrNull { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown person type: $type")
        }
    }
}