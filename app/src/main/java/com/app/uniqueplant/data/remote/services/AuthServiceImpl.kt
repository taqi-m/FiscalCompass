package com.app.uniqueplant.data.remote.services

import com.app.uniqueplant.domain.interfaces.AuthService
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthService {
    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid
}