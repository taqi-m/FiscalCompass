package com.app.uniqueplant.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.usecase.auth.LoginUseCase
import com.app.uniqueplant.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import com.app.uniqueplant.presentation.auth.states.LoginState

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val sessionUseCase: SessionUseCase
) : ViewModel() {

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult.asStateFlow()

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password


    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun resetFields() {
        _email.value = ""
        _password.value = ""
    }

    fun signUp(email: String, password: String) {
        _authResult.value = AuthResult.Loading
        viewModelScope.launch {
            signUpUseCase(email, password).collect { result ->
                _authResult.value = when (result) {
                    is Resource.Loading -> AuthResult.Loading
                    is Resource.Success -> AuthResult.Success
                    is Resource.Error -> AuthResult.Error(result.message ?: "Sign up failed")
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        _authResult.value = AuthResult.Loading
        viewModelScope.launch {
            loginUseCase(email, password).collect { result ->
                _authResult.value = when (result) {
                    is Resource.Loading -> AuthResult.Loading
                    is Resource.Success -> AuthResult.Success
                    is Resource.Error -> AuthResult.Error(result.message ?: "Sign in failed")
                }
            }
        }
    }

    fun logout() {
        sessionUseCase.logout()
    }
    fun isUserLoggedIn(): Boolean = sessionUseCase.isUserLoggedIn()
}
