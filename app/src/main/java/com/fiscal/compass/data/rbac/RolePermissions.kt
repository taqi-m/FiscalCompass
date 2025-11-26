package com.fiscal.compass.data.rbac

object RolePermissions {
    private val rolePermissions = mapOf(
        Role.ADMIN to setOf(
            Permission.VIEW_CATEGORIES,
            Permission.ADD_CATEGORY,
            Permission.EDIT_CATEGORY,
            Permission.DELETE_CATEGORY,
            Permission.VIEW_PERSON,
            Permission.ADD_PERSON,
            Permission.EDIT_PERSON,
            Permission.DELETE_PERSON,
            Permission.VIEW_ALL_TRANSACTIONS,
            Permission.VIEW_OWN_TRANSACTIONS,
            Permission.ADD_TRANSACTION,
            Permission.VIEW_ALL_ANALYTICS,
            Permission.VIEW_OWN_ANALYTICS
        ),
        Role.EMPLOYEE to setOf(
            Permission.VIEW_OWN_TRANSACTIONS,
            Permission.ADD_TRANSACTION,
            Permission.VIEW_OWN_ANALYTICS
        )
    )

    fun hasPermission(role: Role, permission: Permission): Boolean {
        return rolePermissions[role]?.contains(permission) ?: false
    }
}