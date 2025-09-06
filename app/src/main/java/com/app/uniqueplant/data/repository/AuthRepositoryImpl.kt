package com.app.uniqueplant.data.repository

import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.repository.AppPreferenceRepository
import com.app.uniqueplant.domain.repository.AuthRepository
import com.app.uniqueplant.domain.repository.UserRepository
import com.app.uniqueplant.presentation.screens.settings.UserInfo
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
    private val firebaseFirestore: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferenceRepository
) : AuthRepository {

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        val isAlreadyLoggedIn = isUserLoggedIn()
        if (isAlreadyLoggedIn) {
            emit(Resource.Error("User is already logged in"))
            return@flow
        }
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val userType = firebaseFirestore.collection("users")
            .document(result.user?.uid ?: "")
            .get().await().getString("userType")
        if (userType != null) {
            appPreferences.setUserLoggedIn(true)
            appPreferences.setUserType(userType)
            userRepository.addUserToDatabase(
                userId = result.user?.uid ?: "",
                username = result.user?.displayName ?: "",
                email = email,
                userType = userType
            )
            emit(Resource.Success(result))
        } else {
            emit(Resource.Error("User type not found"))
            return@flow
        }
        emit(Resource.Success(result))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred during login"))
    }

    override fun signUpUser(
        name: String, email: String, password: String, userType: String
    ): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val uuid = result.user?.uid
        val user = hashMapOf(
            "name" to name, "email" to email, "userType" to userType
        )
        firebaseFirestore.collection("users").document(uuid ?: "").set(user).await()
        emit(Resource.Success(result))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred during sign up"))
    }

    override fun logout(): Flow<Resource<String>> = flow {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "An error occurred during sign out"))
        } finally {
            appPreferences.setUserLoggedIn(false)
            appPreferences.removeUserType()
            emit(Resource.Success("User logged out successfully"))
        }
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override fun isUserLoggedIn(): Boolean {
        return appPreferences.isUserLoggedIn()
    }

    override fun getUserType(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        var userType: String? = appPreferences.getUserType()
        if (userType != null) {
            emit(Resource.Success(userType))
            return@flow
        }

        val uid = firebaseAuth.currentUser?.uid ?: ""
        if (uid.isEmpty()) {
            emit(Resource.Error("User not logged in"))
            return@flow
        }

        val document = firebaseFirestore.collection("users").document(uid).get().await()
        if (!document.exists()) {
            emit(Resource.Error("User document does not exist"))
        }
        userType = document.getString("userType")
        userType?.let {
            appPreferences.setUserType(it)
            emit(Resource.Success(it))
        } ?: emit(Resource.Error("User type not found"))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred while fetching user type"))
    }

    
    override fun getUserInfo(): Flow<Resource<UserInfo>> = flow {
        emit(Resource.Loading())
        val uid = firebaseAuth.currentUser?.uid ?: ""
        if (uid.isEmpty()) {
            emit(Resource.Error("User not logged in"))
            return@flow
        }

        try {
            val document = firebaseFirestore.collection("users").document(uid).get().await()
            if (!document.exists()) {
                emit(Resource.Error("User document does not exist"))
                return@flow
            }
            val userInfo = UserInfo(
                userName = document.getString("name") ?: "Unknown",
                userEmail = document.getString("email") ?: "Unknown",
                profilePictureUrl = document.getString("profilePictureUrl")
            )

            emit(Resource.Success(userInfo))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred while fetching user info"))
        }
    }

}

