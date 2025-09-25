package com.app.uniqueplant.domain.usecase.rbac

import com.app.uniqueplant.data.rbac.Permission
import com.app.uniqueplant.data.rbac.RolePermissions
import com.app.uniqueplant.domain.repository.AuthRepository
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