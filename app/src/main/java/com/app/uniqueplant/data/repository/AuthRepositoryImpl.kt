package com.app.uniqueplant.data.repository

import com.app.uniqueplant.domain.model.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        emit(Resource.Success(result))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred during login"))
    }

    override fun signUpUser(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        emit(Resource.Success(result))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred during sign up"))
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null
}