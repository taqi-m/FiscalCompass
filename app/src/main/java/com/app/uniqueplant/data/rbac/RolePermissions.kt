package com.app.uniqueplant.data.rbac

object RolePermissions {
    private val rolePermissions = mapOf(
        Role.ADMIN to setOf(
            Permission.VIEW_CATEGORIES,
            Permission.ADD_CATEGORY,
            Permission.EDIT_CATEGORY,
            Permission.DELETE_CATEGORY,
            Permission.ADD_PERSON,
            Permission.EDIT_PERSON,
            Permission.DELETE_PERSON
        ),
        Role.EMPLOYEE to setOf(
            Permission.VIEW_CATEGORIES,
            Permission.VIEW_PERSON
        )
    )

    fun hasPermission(role: Role, permission: Permission): Boolean {
        return rolePermissions[role]?.contains(permission) ?: false
    }
}