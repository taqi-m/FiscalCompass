package com.fiscal.compass.di

import android.util.Log
import com.fiscal.compass.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com. google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger. Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax. inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()

        if (BuildConfig.USE_EMULATOR) {
            try {
                auth.useEmulator(
                    BuildConfig.EMULATOR_HOST,
                    BuildConfig.AUTH_EMULATOR_PORT
                )
                Log.d("FirebaseModule", "✅ Auth Emulator connected: ${BuildConfig.EMULATOR_HOST}:${BuildConfig.AUTH_EMULATOR_PORT}")
            } catch (e: Exception) {
                Log.e("FirebaseModule", "❌ Failed to connect to Auth Emulator", e)
            }
        } else {
            Log.d("FirebaseModule", "🚀 Using Production Firebase Auth")
        }

        return auth
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()

        if (BuildConfig.USE_EMULATOR) {
            try {
                firestore.useEmulator(
                    BuildConfig.EMULATOR_HOST,
                    BuildConfig.FIRESTORE_EMULATOR_PORT
                )

                Log.d("FirebaseModule", "✅ Firestore Emulator connected: ${BuildConfig.EMULATOR_HOST}:${BuildConfig.FIRESTORE_EMULATOR_PORT}")
            } catch (e: Exception) {
                Log.e("FirebaseModule", "❌ Failed to connect to Firestore Emulator", e)
            }
        } else {
            Log. d("FirebaseModule", "🚀 Using Production Firebase Firestore")
        }

        return firestore
    }
}