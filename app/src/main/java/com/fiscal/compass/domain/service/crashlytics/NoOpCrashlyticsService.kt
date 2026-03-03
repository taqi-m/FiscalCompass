package com.fiscal.compass.domain.service.crashlytics

import android.util.Log
import javax.inject.Inject

/**
 * No-op implementation of CrashlyticsService for dev builds.
 * Logs to Logcat for debugging but doesn't send to Firebase.
 */
class NoOpCrashlyticsService @Inject constructor() : CrashlyticsService {

    companion object {
        private const val TAG = "NoOpCrashlytics"
    }

    override fun logException(throwable: Throwable) {
        Log.e(TAG, "[DEV] Exception would be logged", throwable)
    }

    override fun log(message: String) {
        Log.d(TAG, "[DEV] Message would be logged: $message")
    }

    override fun setUserId(userId: String?) {
        Log.d(TAG, "[DEV] User ID would be set: ${userId ?: "null"}")
    }

    override fun setCustomKey(key: String, value: String) {
        Log.d(TAG, "[DEV] Custom key would be set: $key = $value")
    }

    override fun setCustomKey(key: String, value: Boolean) {
        Log.d(TAG, "[DEV] Custom key would be set: $key = $value")
    }

    override fun setCustomKey(key: String, value: Int) {
        Log.d(TAG, "[DEV] Custom key would be set: $key = $value")
    }

    override fun setCustomKey(key: String, value: Long) {
        Log.d(TAG, "[DEV] Custom key would be set: $key = $value")
    }

    override fun setCustomKey(key: String, value: Double) {
        Log.d(TAG, "[DEV] Custom key would be set: $key = $value")
    }

    override fun recordException(message: String, throwable: Throwable) {
        Log.e(TAG, "[DEV] Exception would be recorded: $message", throwable)
    }
}

