package com.fiscal.compass.domain.userBehaviour

import com.fiscal.compass.domain.model.base.User
import com.fiscal.compass.domain.model.rbac.Role
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Factory for creating appropriate UserBehaviorStrategy based on user role.
 * 
 * This factory uses a MAP LOOKUP instead of when statements, achieving:
 * - O(1) strategy selection
 * - No conditional branching
 * - Easy extension (just add to map)
 * - Single source of truth for strategy creation
 * 
 * This is the ONLY place with role-based logic, and it's declarative, not imperative!
 */
@Singleton
class UserBehaviorFactory @Inject constructor(
    adminStrategy: AdminBehaviorStrategy,
    employeeStrategy: EmployeeBehaviorStrategy
) {
    
    /**
     * Pre-built map eliminates when statements at runtime.
     * This is declarative data, not imperative branching logic!
     */
    private val strategies: Map<Role, UserBehaviorStrategy> = mapOf(
        Role.ADMIN to adminStrategy,
        Role.EMPLOYEE to employeeStrategy
    )
    
    /**
     * Returns the appropriate strategy for the given role.
     * Uses map lookup (O(1)) instead of when statement.
     * @param role The user's role
     * @return UserBehaviorStrategy for that role
     * @throws IllegalArgumentException if role is not mapped
     */
    fun getStrategy(role: Role): UserBehaviorStrategy {
        return strategies[role] 
            ?: throw IllegalArgumentException("No strategy found for role: $role")
    }
    
    /**
     * Convenience method to get strategy directly from User object.
     * @param user The user object
     * @return UserBehaviorStrategy for that user's role
     */
    fun getStrategy(user: User): UserBehaviorStrategy {
        return getStrategy(user.userType)
    }
}
