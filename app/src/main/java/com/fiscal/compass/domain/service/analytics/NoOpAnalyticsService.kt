package com.fiscal.compass.domain.service.analytics

import android.util.Log
import javax.inject.Inject

/**
 * No-op implementation of AnalyticsService for dev builds.
 * Logs events to Logcat for debugging but doesn't send to Firebase.
 */
class NoOpAnalyticsService @Inject constructor() : AnalyticsService {

    companion object {
        private const val TAG = "NoOpAnalytics"
    }

    override fun logEvent(event: AnalyticsEvent) {
        Log.d(TAG, "[DEV] Event would be logged: ${event.eventName}")
    }

    override fun logScreenView(screenName: String, screenClass: String?) {
        Log.d(TAG, "[DEV] Screen view would be logged: $screenName")
    }

    override fun setUserId(userId: String?) {
        Log.d(TAG, "[DEV] User ID would be set: ${userId ?: "null"}")
    }

    override fun setUserProperty(name: String, value: String?) {
        Log.d(TAG, "[DEV] User property would be set: $name = ${value ?: "null"}")
    }

    override fun resetAnalyticsData() {
        Log.d(TAG, "[DEV] Analytics data would be reset")
    }
}

