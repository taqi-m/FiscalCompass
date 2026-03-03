package com.fiscal.compass.domain.usecase.user

import com.fiscal.compass.domain.model.Resource
import com.fiscal.compass.domain.model.rbac.Role
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Use case for creating a new user via Firebase Cloud Function.
 * This allows admins to create users without affecting their own session.
 */
class CreateUserUseCase @Inject constructor(
    private val functions: FirebaseFunctions
) {
    /**
     * Creates a new user with the specified details.
     *
     * @param name The display name for the new user
     * @param email The email address for the new user
     * @param password The password for the new user
     * @param role The role to assign to the new user (ADMIN or EMPLOYEE)
     * @return Flow emitting Resource states (Loading, Success, Error)
     */
    operator fun invoke(
        name: String,
        email: String,
        password: String,
        role: Role
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {
            val data = hashMapOf(
                "name" to name,
                "email" to email,
                "password" to password,
                "userType" to role.name.lowercase()
            )

            val result = functions
                .getHttpsCallable("createUser")
                .call(data)
                .await()

            @Suppress("UNCHECKED_CAST")
            val responseData = result.data as? Map<String, Any>
            val userId = responseData?.get("uid") as? String

            if (userId != null) {
                emit(Resource.Success(userId))
            } else {
                emit(Resource.Error("Failed to create user: No user ID returned"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create user"))
        }
    }
}

