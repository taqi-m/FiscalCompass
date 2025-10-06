package com.app.uniqueplant.domain.usecase.auth

import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.repository.AppPreferenceRepository
import com.app.uniqueplant.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val appPreferences: AppPreferenceRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        val isLoggedIn = appPreferences.isUserLoggedIn()
        if (isLoggedIn) {
            return Result.failure(Exception("User is already logged in"))
        }

        val isEmailValid = isEmailValid(email)
        if (!isEmailValid) {
            return Result.failure(Exception("Invalid email format"))
        }

        val loginResult = authRepository.loginUser(email, password)
        return when (loginResult) {
            is Resource.Success -> {
                Result.success("Login successful")
            }

            is Resource.Error -> {
                Result.failure(Exception(loginResult.message ?: "Login failed"))
            }

            else -> {
                Result.failure(Exception("Unexpected error during login"))
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.+)(\\.)(.+)"
        return email.matches(emailRegex.toRegex())
    }
}
