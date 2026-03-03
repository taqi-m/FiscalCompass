package com.fiscal.compass.domain.service.crashlytics

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

/**
 * Production implementation of CrashlyticsService using Firebase Crashlytics.
 */
class FirebaseCrashlyticsService @Inject constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics
) : CrashlyticsService {

    companion object {
        private const val TAG = "FirebaseCrashlytics"
    }

    override fun logException(throwable: Throwable) {
        firebaseCrashlytics.recordException(throwable)
        Log.e(TAG, "Exception logged", throwable)
    }

    override fun log(message: String) {
        firebaseCrashlytics.log(message)
        Log.d(TAG, "Message logged: $message")
    }

    override fun setUserId(userId: String?) {
        userId?.let { firebaseCrashlytics.setUserId(it) }
        Log.d(TAG, "User ID set: ${userId ?: "null"}")
    }

    override fun setCustomKey(key: String, value: String) {
        firebaseCrashlytics.setCustomKey(key, value)
        Log.d(TAG, "Custom key set: $key = $value")
    }

    override fun setCustomKey(key: String, value: Boolean) {
        firebaseCrashlytics.setCustomKey(key, value)
        Log.d(TAG, "Custom key set: $key = $value")
    }

    override fun setCustomKey(key: String, value: Int) {
        firebaseCrashlytics.setCustomKey(key, value)
        Log.d(TAG, "Custom key set: $key = $value")
    }

    override fun setCustomKey(key: String, value: Long) {
        firebaseCrashlytics.setCustomKey(key, value)
        Log.d(TAG, "Custom key set: $key = $value")
    }

    override fun setCustomKey(key: String, value: Double) {
        firebaseCrashlytics.setCustomKey(key, value)
        Log.d(TAG, "Custom key set: $key = $value")
    }

    override fun recordException(message: String, throwable: Throwable) {
        firebaseCrashlytics.log(message)
        firebaseCrashlytics.recordException(throwable)
        Log.e(TAG, "Exception recorded: $message", throwable)
    }
}

