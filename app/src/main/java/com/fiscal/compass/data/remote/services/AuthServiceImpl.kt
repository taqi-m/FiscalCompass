package com.fiscal.compass.data.remote.services

import com.fiscal.compass.domain.interfaces.AuthService
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthService {
    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid
}