package com.fiscal.compass.domain.service.analytics

/**
 * Abstraction layer for analytics tracking.
 * Allows for easy swapping of analytics providers and no-op implementation for dev builds.
 */
interface AnalyticsService {

    /**
     * Log a custom event with optional parameters
     */
    fun logEvent(event: AnalyticsEvent)

    /**
     * Log a screen view event
     */
    fun logScreenView(screenName: String, screenClass: String? = null)

    /**
     * Set the user ID for analytics tracking
     */
    fun setUserId(userId: String?)

    /**
     * Set a custom user property
     */
    fun setUserProperty(name: String, value: String?)

    /**
     * Reset analytics data (e.g., on logout)
     */
    fun resetAnalyticsData()
}

