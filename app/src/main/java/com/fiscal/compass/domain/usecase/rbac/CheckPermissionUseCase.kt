package com.fiscal.compass.domain.usecase.rbac

import com.fiscal.compass.data.rbac.Permission
import com.fiscal.compass.data.rbac.RolePermissions
import com.fiscal.compass.domain.repository.AuthRepository
import javax.inject.Inject

class CheckPermissionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(permission: Permission): Boolean {
        val role = authRepository.getUserRole()
        role ?: return false
        return RolePermissions.hasPermission(role, permission)
    }
}