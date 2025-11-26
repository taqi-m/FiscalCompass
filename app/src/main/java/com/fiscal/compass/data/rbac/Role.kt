package com.fiscal.compass.data.rbac

enum class Role {
    ADMIN,
    EMPLOYEE;

    companion object {
        fun fromString(value: String?): Role {
            return when (value?.lowercase()) {
                "admin" -> ADMIN
                "employee" -> EMPLOYEE
                else -> EMPLOYEE // Default to employee for safety
            }
        }
    }
}