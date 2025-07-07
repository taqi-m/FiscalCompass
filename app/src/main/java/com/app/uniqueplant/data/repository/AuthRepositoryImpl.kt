package com.app.uniqueplant.data.repository

import com.app.uniqueplant.domain.model.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AuthRepository {

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        emit(Resource.Success(result))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred during login"))
    }

    override fun signUpUser(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val uuid = result.user?.uid
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "userType" to "user" // Default user type, can be changed later
        )
        firebaseFirestore.collection("users").document(uuid ?: "").set(user).await()
        emit(Resource.Success(result))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred during sign up"))
    }

    override fun logout() {
        firebaseAuth.signOut(

        )
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun getUserType(): Flow<Resource<String>> = flow {
        val uid = firebaseAuth.currentUser?.uid ?: ""
        if (uid.isEmpty()) {
            emit(Resource.Error("User not logged in"))
            return@flow
        }
        val document = firebaseFirestore.collection("users").document(uid).get().await()
        if (document.exists()) {
            val userType = document.getString("userType")
            if (userType != null) {
                emit(Resource.Success(userType))
            } else {
                emit(Resource.Error("User type not found"))
            }
        } else {
            emit(Resource.Error("User document does not exist"))
        }
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred while fetching user type"))
    }


}