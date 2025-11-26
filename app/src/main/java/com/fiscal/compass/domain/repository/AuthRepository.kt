package com.fiscal.compass.domain.repository

import com.fiscal.compass.data.rbac.Role
import com.fiscal.compass.domain.model.Resource
import com.fiscal.compass.domain.model.dto.UserInfo
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginUser(email: String, password: String): Resource<AuthResult>
    fun signUpUser(name: String, email: String, password: String, userType: String = "employee"): Flow<Resource<AuthResult>>
    fun logout(): Flow<Resource<String>>
    fun getCurrentUser(): FirebaseUser?
    fun isUserLoggedIn(): Boolean

    fun getUserType(): Flow<Resource<String>>

    fun getUserRole() : Role?

    suspend fun getUserInfo(): Resource<UserInfo>
}


