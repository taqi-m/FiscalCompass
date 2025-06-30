package com.app.uniqueplant.domain.usecase.auth

import com.app.uniqueplant.data.repository.AuthRepository
import javax.inject.Inject

class SessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun logout() {
        authRepository.logout()
    }
}