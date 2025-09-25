package com.app.uniqueplant.data.rbac

object RolePermissions {
    private val rolePermissions = mapOf(
        Role.ADMIN to setOf(
            Permission.VIEW_CATEGORIES,
            Permission.ADD_CATEGORY,
            Permission.EDIT_CATEGORY,
            Permission.DELETE_CATEGORY
        ),
        Role.EMPLOYEE to setOf(
            Permission.VIEW_CATEGORIES
        )
    )

    fun hasPermission(role: Role, permission: Permission): Boolean {
        return rolePermissions[role]?.contains(permission) ?: false
    }
}