package com.fiscal.compass.domain.model.rbac

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
            Permission.EDIT_TRANSACTION,
            Permission.DELETE_TRANSACTION,
            Permission.VIEW_ALL_ANALYTICS,
            Permission.VIEW_OWN_ANALYTICS,
            Permission.SYNC_ALL_USERS_DATA,
            Permission.SYNC_OWN_DATA,
            Permission.MANAGE_USERS
        ),
        Role.EMPLOYEE to setOf(
            Permission.VIEW_OWN_TRANSACTIONS,
            Permission.ADD_TRANSACTION,
            Permission.VIEW_OWN_ANALYTICS,
            Permission.SYNC_OWN_DATA
        )
    )

    fun hasPermission(role: Role, permission: Permission): Boolean {
        return rolePermissions[role]?.contains(permission) ?: false
    }
}