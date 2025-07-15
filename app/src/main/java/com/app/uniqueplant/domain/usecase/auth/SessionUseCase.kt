package com.app.uniqueplant.domain.usecase.auth

import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun logout(): Flow<Resource<String>> {
        return authRepository.logout()
    }

    fun getCurrentUser() = authRepository.getCurrentUser()

    fun getUserType(): Flow<Resource<String>> {
        return authRepository.getUserType()
    }
}