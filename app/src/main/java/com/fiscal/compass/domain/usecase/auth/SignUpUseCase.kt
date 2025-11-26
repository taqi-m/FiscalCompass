package com.fiscal.compass.domain.usecase.auth

import com.fiscal.compass.domain.model.Resource
import com.fiscal.compass.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(name: String, email: String, password: String): Flow<Resource<AuthResult>> {
        return authRepository.signUpUser(
            name = name,
            email = email,
            password = password
        )
    }
}

