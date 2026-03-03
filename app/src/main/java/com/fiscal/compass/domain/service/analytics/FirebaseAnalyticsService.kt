package com.fiscal.compass.domain.service.analytics

import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/**
 * Production implementation of AnalyticsService using Firebase Analytics.
 */
class FirebaseAnalyticsService @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsService {

    companion object {
        private const val TAG = "FirebaseAnalytics"
    }

    override fun logEvent(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.eventName, event.params)
        Log.d(TAG, "Event logged: ${event.eventName}")
    }

    override fun logScreenView(screenName: String, screenClass: String?) {
        val params = android.os.Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClass?.let { putString(FirebaseAnalytics.Param.SCREEN_CLASS, it) }
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
        Log.d(TAG, "Screen view logged: $screenName")
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
        Log.d(TAG, "User ID set: ${userId ?: "null"}")
    }

    override fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value)
        Log.d(TAG, "User property set: $name = ${value ?: "null"}")
    }

    override fun resetAnalyticsData() {
        firebaseAnalytics.resetAnalyticsData()
        Log.d(TAG, "Analytics data reset")
    }
}

