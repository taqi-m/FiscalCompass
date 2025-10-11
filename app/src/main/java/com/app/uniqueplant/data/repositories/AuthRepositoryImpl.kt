package com.app.uniqueplant.data.repositories

import com.app.uniqueplant.data.local.model.UserEntity
import com.app.uniqueplant.data.rbac.Role
import com.app.uniqueplant.domain.model.dataModels.Resource
import com.app.uniqueplant.domain.model.dtos.UserInfo
import com.app.uniqueplant.domain.repository.AppPreferenceRepository
import com.app.uniqueplant.domain.repository.AuthRepository
import com.app.uniqueplant.domain.repository.UserRepository
import com.google.firebase.Timestamp
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

    override suspend fun loginUser(email: String, password: String): Resource<AuthResult> {
        try {
            val currentTimestamp = Timestamp.now().toDate().time
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user == null) {
                return Resource.Error("Authentication failed: User is null")
            }
            val uuid = user.uid
            val userInfo = firebaseFirestore.collection("users")
                .document(uuid)
                .get().await()
            val userType = userInfo.getString("userType")
            val userName = userInfo.getString("name")
            val userEmail = userInfo.getString("email")
            if (userType != null && userName != null && userEmail != null) {
                appPreferences.setUserLoggedIn(true)
                appPreferences.setUserType(userType)
                appPreferences.setUserInfo(userName, userEmail)
                userRepository.addUser(
                    UserEntity(
                        userId = uuid,
                        username = userName,
                        email = userEmail,
                        userType = userType,
                        lastLoginAt = currentTimestamp
                    )
                )
                return (Resource.Success(result))
            } else {
                return (Resource.Error("User data is incomplete"))
            }
        } catch (e: Exception) {
            return (Resource.Error(e.message ?: "An error occurred during login"))
        }
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

    override fun getUserRole(): Role? {
        val userType = appPreferences.getUserType() ?: return null
        return Role.fromString(userType)
    }


    override suspend fun getUserInfo(): Resource<UserInfo> {
        try {
            // Get current user's UUID
            val uuid = firebaseAuth.currentUser?.uid
            if (uuid.isNullOrEmpty()) {
                return Resource.Error("User not logged in")
            }

            // Attempt to get user info from cache
            val cachedInfo = appPreferences.getUserInfo()
            if (cachedInfo.first != "User" || cachedInfo.second.isNotEmpty()) {
                return Resource.Success(
                    UserInfo(
                        userName = cachedInfo.first, email = cachedInfo.second, uuid = uuid,
                    )
                )
            }

            // If cache is empty, fetch from Firestore
            val document = firebaseFirestore.collection("users").document(uuid).get().await()
            if (!document.exists()) {
                return Resource.Error("User document does not exist")
            }
            val userInfo = UserInfo(
                userName = document.getString("name") ?: "Unknown",
                email = document.getString("email") ?: "Unknown",
                profilePicUrl = document.getString("profilePictureUrl"),
                uuid = uuid
            )
            return Resource.Success(userInfo)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "An error occurred while fetching user info")
        }
    }

}

